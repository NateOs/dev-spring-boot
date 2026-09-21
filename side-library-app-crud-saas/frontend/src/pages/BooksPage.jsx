import { useEffect, useState } from "react";
import { api } from "../api/client.js";
import ErrorBanner from "../components/ErrorBanner.jsx";

const emptyForm = { title: "", isbn: "", genre: "", publishedYear: "", totalCopies: 1, authorId: "" };

export default function BooksPage() {
  const [page, setPage] = useState({ content: [], totalPages: 0, number: 0 });
  const [authors, setAuthors] = useState([]);
  const [query, setQuery] = useState("");
  const [pageNumber, setPageNumber] = useState(0);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadBooks = (search = query, pageNum = pageNumber) => {
    setLoading(true);
    const params = new URLSearchParams({ page: pageNum, size: 8 });
    if (search) params.set("query", search);
    api
      .get(`/books?${params.toString()}`)
      .then(setPage)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadBooks();
    api.get("/authors").then(setAuthors).catch((e) => setError(e.message));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    setPageNumber(0);
    loadBooks(query, 0);
  };

  const goToPage = (n) => {
    setPageNumber(n);
    loadBooks(query, n);
  };

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.post("/books", {
        ...form,
        publishedYear: form.publishedYear ? Number(form.publishedYear) : null,
        totalCopies: Number(form.totalCopies),
        authorId: Number(form.authorId),
      });
      setForm(emptyForm);
      loadBooks(query, pageNumber);
    } catch (e) {
      setError(e.message);
    }
  };

  const handleDelete = async (book) => {
    if (!confirm(`Delete "${book.title}"?`)) return;
    setError(null);
    try {
      await api.del(`/books/${book.id}`);
      loadBooks(query, pageNumber);
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Books</h1>
          <p>Catalog of titles, searchable by title or author.</p>
        </div>
      </div>

      <ErrorBanner message={error} />

      <div className="card">
        <form className="form-grid" onSubmit={handleSubmit}>
          <div>
            <label>Title</label>
            <input value={form.title} onChange={handleChange("title")} required />
          </div>
          <div>
            <label>ISBN</label>
            <input value={form.isbn} onChange={handleChange("isbn")} required />
          </div>
          <div>
            <label>Genre</label>
            <input value={form.genre} onChange={handleChange("genre")} placeholder="Optional" />
          </div>
          <div>
            <label>Year</label>
            <input type="number" value={form.publishedYear} onChange={handleChange("publishedYear")} />
          </div>
          <div>
            <label>Copies</label>
            <input type="number" min="1" value={form.totalCopies} onChange={handleChange("totalCopies")} required />
          </div>
          <div>
            <label>Author</label>
            <select value={form.authorId} onChange={handleChange("authorId")} required>
              <option value="" disabled>Select author</option>
              {authors.map((a) => (
                <option key={a.id} value={a.id}>{a.firstName} {a.lastName}</option>
              ))}
            </select>
          </div>
          <button type="submit">Add book</button>
        </form>
      </div>

      <div className="card">
        <form onSubmit={handleSearch} style={{ marginBottom: "1rem", display: "flex", gap: "0.5rem" }}>
          <input
            placeholder="Search by title or author…"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <button type="submit" className="secondary">Search</button>
        </form>

        {loading ? (
          <p className="muted">Loading…</p>
        ) : page.content.length === 0 ? (
          <p className="empty-state">No books found.</p>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Author</th>
                  <th>Genre</th>
                  <th>Year</th>
                  <th>Available</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {page.content.map((b) => (
                  <tr key={b.id}>
                    <td>{b.title}</td>
                    <td>{b.authorName}</td>
                    <td className="muted">{b.genre || "—"}</td>
                    <td className="muted">{b.publishedYear || "—"}</td>
                    <td>{b.availableCopies} / {b.totalCopies}</td>
                    <td className="actions">
                      <button className="danger small" onClick={() => handleDelete(b)}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="pagination">
              <button
                className="secondary small"
                disabled={pageNumber === 0}
                onClick={() => goToPage(pageNumber - 1)}
              >
                Prev
              </button>
              <span className="muted">
                Page {page.number + 1} of {Math.max(page.totalPages, 1)}
              </span>
              <button
                className="secondary small"
                disabled={pageNumber + 1 >= page.totalPages}
                onClick={() => goToPage(pageNumber + 1)}
              >
                Next
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
