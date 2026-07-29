import { Link, Outlet } from "react-router-dom";

export function DashboardLayout() {
  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <h2>HMS</h2>

        <nav>
          <Link to="/admin/dashboard">Dashboard</Link>
          <Link to="/admin/users">Users</Link>
          <Link to="/admin/users/create">Create User</Link>
        </nav>
      </aside>

      <main className="dashboard-main">
        <header className="dashboard-header">
          <h3>Admin Panel</h3>
          <button>Logout</button>
        </header>

        <section className="dashboard-content">
          <Outlet />
        </section>
      </main>
    </div>
  );
}