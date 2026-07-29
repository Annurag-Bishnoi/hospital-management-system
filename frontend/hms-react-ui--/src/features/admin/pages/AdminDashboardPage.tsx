export function AdminDashboardPage() {
    return (
      <div>
        <h1>Admin Dashboard</h1>
  
        <div className="summary-grid">
          <div className="summary-card">
            <h3>Total Users</h3>
            <p>0</p>
          </div>
  
          <div className="summary-card">
            <h3>Active Users</h3>
            <p>0</p>
          </div>
  
          <div className="summary-card">
            <h3>Locked Users</h3>
            <p>0</p>
          </div>
  
          <div className="summary-card">
            <h3>Total Roles</h3>
            <p>0</p>
          </div>
        </div>
      </div>
    );
  }