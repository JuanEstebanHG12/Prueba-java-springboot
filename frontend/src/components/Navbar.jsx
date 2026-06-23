import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { token, role, user, isAdmin, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <NavLink to="/" className="navbar-brand">
        TalentBoard
      </NavLink>

      <div className="navbar-links">
        {token ? (
          <>
            {/* Navigation links differ per role so each user only sees what is relevant to them */}
            {isAdmin && (
              <>
                <NavLink to="/admin/vacancies" className="navbar-link">
                  Vacancies
                </NavLink>
                <NavLink to="/admin/applications" className="navbar-link">
                  Applications
                </NavLink>
                <NavLink to="/admin/interviews" className="navbar-link">
                  Interviews
                </NavLink>
              </>
            )}

            {role === 'RECRUITER' && (
              // Recruiters get a read-only view of vacancies — no candidate features
              <NavLink to="/vacancies" className="navbar-link">
                Vacancies
              </NavLink>
            )}

            {role === 'CANDIDATE' && (
              <>
                <NavLink to="/vacancies" className="navbar-link">
                  Vacancies
                </NavLink>
                <NavLink to="/my-applications" className="navbar-link">
                  My Applications
                </NavLink>
              </>
            )}

            <span className="navbar-user">
              Hi, {user?.name ?? 'User'}
            </span>
            <span
              className="navbar-user text-muted"
              style={{ fontSize: '0.75rem', padding: '0.2rem 0.5rem', background: '#f1f5f9', borderRadius: '9999px' }}
            >
              {role}
            </span>
            <button className="btn btn-ghost btn-sm" onClick={handleLogout}>
              Logout
            </button>
          </>
        ) : (
          <>
            <NavLink to="/login" className="navbar-link">
              Login
            </NavLink>
            <NavLink to="/register" className="btn btn-primary btn-sm">
              Register
            </NavLink>
          </>
        )}
      </div>
    </nav>
  )
}
