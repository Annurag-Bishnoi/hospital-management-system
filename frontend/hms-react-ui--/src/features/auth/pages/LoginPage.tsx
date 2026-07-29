import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiClient } from "../../../shared/services/apiClient";

export function LoginPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleLogin(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");

    try {
      const response = await apiClient.post("/auth/login", {
        username,
        password,
      });

      const data = response.data;

      if (data.success && data.role === "ADMIN") {
        navigate("/admin/dashboard");
      } else {
        setError("Only admin can access this dashboard.");
      }
    } catch (error) {
      setError("Invalid username or password.");
    }
  }

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleLogin}>
        <h1>HMS Login</h1>
        <p>Hospital Management System</p>

        <label>Username</label>
        <input
          type="text"
          placeholder="Enter username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
        />

        <label>Password</label>
        <input
          type="password"
          placeholder="Enter password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />

        {error && <p className="error-message">{error}</p>}

        <button type="submit">Login</button>
      </form>
    </div>
  );
}