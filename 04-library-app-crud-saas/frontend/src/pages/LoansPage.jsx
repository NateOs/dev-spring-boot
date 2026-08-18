import { useEffect, useState } from "react";
import { api } from "../api/client.js";
import ErrorBanner from "../components/ErrorBanner.jsx";

export default function LoansPage() {
  const [loans, setLoans] = useState([]);
  const [books, setBooks] = useState([]);
  const [members, setMembers] = useState([]);
  const [form, setForm] = useState({ bookId: "", memberId: "" });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api.get("/loans").then(setLoans).catch((e) => setError(e.message));
  };

  const loadReferenceData = () => {
    api.get("/books?size=200").then((page) => setBooks(page.content)).catch((e) => setError(e.message));
    api.get("/members").then(setMembers).catch((e) => setError(e.message));
  };

  useEffect(() => {
    setLoading(true);
    Promise.all([api.get("/loans"), api.get("/books?size=200"), api.get("/members")])
      .then(([loanData, bookPage, memberData]) => {
        setLoans(loanData);
        setBooks(bookPage.content);
        setMembers(memberData);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleCheckout = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.post("/loans/checkout", {
        bookId: Number(form.bookId),
        memberId: Number(form.memberId),
      });
      setForm({ bookId: "", memberId: "" });
      load();
      loadReferenceData();
    } catch (e) {
      setError(e.message);
    }
  };

  const handleReturn = async (loan) => {
    setError(null);
    try {
      await api.post(`/loans/${loan.id}/return`, {});
      load();
      loadReferenceData();
    } catch (e) {
      setError(e.message);
    }
  };

  const badgeClass = (status) => {
    if (status === "OVERDUE") return "badge overdue";
    if (status === "RETURNED") return "badge returned";
    return "badge active";
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Loans</h1>
          <p>Check out books to members and record returns.</p>
        </div>
      </div>

      <ErrorBanner message={error} />

      <div className="card">
        <form className="form-grid" onSubmit={handleCheckout}>
          <div>
            <label>Book</label>
            <select value={form.bookId} onChange={handleChange("bookId")} required>
              <option value="" disabled>Select book</option>
              {books.map((b) => (
                <option key={b.id} value={b.id} disabled={b.availableCopies === 0}>
                  {b.title} ({b.availableCopies} available)
                </option>
              ))}
            </select>
          </div>
          <div>
            <label>Member</label>
            <select value={form.memberId} onChange={handleChange("memberId")} required>
              <option value="" disabled>Select member</option>
              {members.map((m) => (
                <option key={m.id} value={m.id}>{m.firstName} {m.lastName}</option>
              ))}
            </select>
          </div>
          <button type="submit">Check out</button>
        </form>
      </div>

      <div className="card">
        {loading ? (
          <p className="muted">Loading…</p>
        ) : loans.length === 0 ? (
          <p className="empty-state">No loans yet.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Book</th>
                <th>Member</th>
                <th>Loaned</th>
                <th>Due</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {loans.map((l) => (
                <tr key={l.id}>
                  <td>{l.bookTitle}</td>
                  <td>{l.memberName}</td>
                  <td className="muted">{l.loanDate}</td>
                  <td className="muted">{l.dueDate}</td>
                  <td><span className={badgeClass(l.status)}>{l.status}</span></td>
                  <td className="actions">
                    {(l.status === "ACTIVE" || l.status === "OVERDUE") && (
                      <button className="secondary small" onClick={() => handleReturn(l)}>
                        Return
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
