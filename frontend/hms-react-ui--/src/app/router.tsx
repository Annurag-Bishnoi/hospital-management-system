import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { AdminDashboardPage } from "../features/admin/pages/AdminDashboardPage.tsx";
import { DashboardLayout } from "../layouts/DashboardLayout.tsx";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <LoginPage />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/admin",
    element: <DashboardLayout />,
    children: [
      {
        path: "dashboard",
        element: <AdminDashboardPage />,
      },
    ],
  },
]);