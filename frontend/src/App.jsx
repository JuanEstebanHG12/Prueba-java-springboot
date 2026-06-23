import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import AdminRoute from './components/AdminRoute'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import HomePage from './pages/HomePage'
import VacanciesPage from './pages/admin/VacanciesPage'
import ApplicationsPage from './pages/admin/ApplicationsPage'
import InterviewsPage from './pages/admin/InterviewsPage'
import VacanciesListPage from './pages/VacanciesListPage'
import MyApplicationsPage from './pages/candidate/MyApplicationsPage'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Protected routes — any logged-in user can reach these */}
          <Route element={<ProtectedRoute />}>
            {/* Visible to candidates, recruiters, and admins alike */}
            <Route path="/vacancies" element={<VacanciesListPage />} />

            {/* Candidate-only: the page itself redirects non-candidates to / */}
            <Route path="/my-applications" element={<MyApplicationsPage />} />

            {/* Admin-only routes */}
            <Route element={<AdminRoute />}>
              <Route path="/admin/vacancies" element={<VacanciesPage />} />
              <Route path="/admin/applications" element={<ApplicationsPage />} />
              <Route path="/admin/interviews" element={<InterviewsPage />} />
            </Route>
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
