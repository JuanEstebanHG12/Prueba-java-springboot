import { Link, Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Feature highlights shown on the public landing — describes what the platform does
const FEATURES = [
  {
    icon: '📋',
    title: 'Post Vacancies',
    description: 'Create and manage job openings across all departments with ease.',
  },
  {
    icon: '👥',
    title: 'Track Applications',
    description: 'Review every candidate application in one centralised place.',
  },
  {
    icon: '🗓',
    title: 'Schedule Interviews',
    description: 'Coordinate interviews and record results seamlessly.',
  },
]

export default function HomePage() {
  const { token } = useAuth()

  // Logged-in users land directly on the vacancies list — no need for a second home
  if (token) {
    return <Navigate to="/vacancies" replace />
  }

  return (
    <div className="page">
      <div className="hero">
        <h1>TalentBoard</h1>
        <p>The all-in-one platform to manage your recruitment — from job postings to final decisions.</p>
        <div className="hero-actions">
          <Link to="/login" className="btn btn-white">Sign in</Link>
          <Link to="/register" className="btn btn-outline-white">Get started</Link>
        </div>
      </div>

      {/* Feature cards replace the old internal stats (module name, counts) */}
      <div className="stats-grid" style={{ marginBottom: '2rem' }}>
        {FEATURES.map((f) => (
          <div key={f.title} className="stat-card">
            <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>{f.icon}</div>
            <div style={{ fontWeight: 700, fontSize: '1rem', marginBottom: '0.35rem', color: 'var(--text)' }}>
              {f.title}
            </div>
            <div className="text-muted" style={{ fontSize: '0.85rem', lineHeight: '1.4' }}>
              {f.description}
            </div>
          </div>
        ))}
      </div>

      <div className="card" style={{ textAlign: 'center', padding: '2rem' }}>
        <p style={{ fontSize: '1.05rem', marginBottom: '1.25rem', color: 'var(--muted)' }}>
          Ready to streamline your hiring process?
        </p>
        <Link to="/register" className="btn btn-primary">
          Create a free account
        </Link>
      </div>
    </div>
  )
}
