import { useEffect, useState } from "react";
import { api } from "../api/client.js";
import ErrorBanner from "../components/ErrorBanner.jsx";

const emptyForm = { firstName: "", lastName: "", bio: "" };

export default function AuthorsPage() {
  const [authors, setAuthors] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api.get("/authors").then(setAuthors).catch((e) => setError(e.message));
  };

  useEffect(() => {
    setLoading(true);
    api
      .get("/authors")
      .then(setAuthors)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.post("/authors", form);
      setForm(emptyForm);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const handleDelete = async (author) => {
    if (!confirm(`Delete ${author.firstName} ${author.lastName}?`)) return;
    setError(null);
    try {
      await api.del(`/authors/${author.id}`);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Authors</h1>
          <p>Authors referenced by books in the catalog.</p>
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
          <div style={{ gridColumn: "span 2" }}>
            <label>Bio</label>
            <input value={form.bio} onChange={handleChange("bio")} placeholder="Optional" />
          </div>
          <button type="submit">Add author</button>
        </form>
      </div>

      <div className="card">
        {loading ? (
          <p className="muted">Loading…</p>
        ) : authors.length === 0 ? (
          <p className="empty-state">No authors yet. Add one above.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Bio</th>
                <th>Books</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {authors.map((a) => (
                <tr key={a.id}>
                  <td>{a.firstName} {a.lastName}</td>
                  <td className="muted">{a.bio || "—"}</td>
                  <td>{a.bookCount}</td>
                  <td className="actions">
                    <button className="danger small" onClick={() => handleDelete(a)}>
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
