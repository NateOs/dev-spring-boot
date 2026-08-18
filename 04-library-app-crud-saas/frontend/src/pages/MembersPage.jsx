import { useEffect, useState } from "react";
import { api } from "../api/client.js";
import ErrorBanner from "../components/ErrorBanner.jsx";

const emptyForm = { firstName: "", lastName: "", email: "" };

export default function MembersPage() {
  const [members, setMembers] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api.get("/members").then(setMembers).catch((e) => setError(e.message));
  };

  useEffect(() => {
    setLoading(true);
    api
      .get("/members")
      .then(setMembers)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.post("/members", form);
      setForm(emptyForm);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const handleDelete = async (member) => {
    if (!confirm(`Remove ${member.firstName} ${member.lastName}?`)) return;
    setError(null);
    try {
      await api.del(`/members/${member.id}`);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Members</h1>
          <p>People who can borrow books.</p>
        </div>
      </div>

      <ErrorBanner message={error} />

      <div className="card">
        <form className="form-grid" onSubmit={handleSubmit}>
          <div>
            <label>First name</label>
            <input value={form.firstName} onChange={handleChange("firstName")} required />
          </div>
          <div>
            <label>Last name</label>
            <input value={form.lastName} onChange={handleChange("lastName")} required />
          </div>
          <div>
            <label>Email</label>
            <input type="email" value={form.email} onChange={handleChange("email")} required />
          </div>
          <button type="submit">Add member</button>
        </form>
      </div>

      <div className="card">
        {loading ? (
          <p className="muted">Loading…</p>
        ) : members.length === 0 ? (
          <p className="empty-state">No members yet. Add one above.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Joined</th>
                <th>Active loans</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {members.map((m) => (
                <tr key={m.id}>
                  <td>{m.firstName} {m.lastName}</td>
                  <td className="muted">{m.email}</td>
                  <td className="muted">{m.joinDate}</td>
                  <td>{m.activeLoanCount}</td>
                  <td className="actions">
                    <button className="danger small" onClick={() => handleDelete(m)}>
                      Delete
                    </button>
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
