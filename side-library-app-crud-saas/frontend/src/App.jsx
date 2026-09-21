import { NavLink, Route, Routes, Navigate } from "react-router-dom";
import BooksPage from "./pages/BooksPage.jsx";
import AuthorsPage from "./pages/AuthorsPage.jsx";
import MembersPage from "./pages/MembersPage.jsx";
import LoansPage from "./pages/LoansPage.jsx";

const links = [
  { to: "/books", label: "Books" },
  { to: "/authors", label: "Authors" },
  { to: "/members", label: "Members" },
  { to: "/loans", label: "Loans" },
];

export default function App() {
  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">📚 Library</div>
        <nav>
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) => (isActive ? "nav-link active" : "nav-link")}
            >
              {link.label}
            </NavLink>
          ))}
        </nav>
      </header>

      <main className="content">
        <Routes>
          <Route path="/" element={<Navigate to="/books" replace />} />
          <Route path="/books" element={<BooksPage />} />
          <Route path="/authors" element={<AuthorsPage />} />
          <Route path="/members" element={<MembersPage />} />
          <Route path="/loans" element={<LoansPage />} />
        </Routes>
      </main>
    </div>
  );
}
