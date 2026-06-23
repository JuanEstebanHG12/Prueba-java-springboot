import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { getApplicationsByCandidate } from '../../api/applicationsApi'
import { useAuth } from '../../context/AuthContext'

// Map each application status to a badge colour so candidates can tell at a glance
const STATUS_BADGE = {
  ACCEPTED:             'badge-green',
  INTERVIEW_SCHEDULED:  'badge-blue',
  UNDER_REVIEW:         'badge-blue',
  REJECTED:             'badge-red',
  WITHDRAWN:            'badge-red',
  PENDING:              'badge-gray',
}

export default function MyApplicationsPage() {
  const { role, user } = useAuth()

  const [applications, setApplications] = useState([])
  const [loading, setLoading]           = useState(true)
  const [error, setError]               = useState('')

  // Guard: only candidates should reach this page
  if (role !== 'CANDIDATE') {
    return <Navigate to="/" replace />
  }

  useEffect(() => {
    fetchApplications()
  }, [])

  async function fetchApplications() {
    setLoading(true)
    setError('')
    try {
      // jti is the user's numeric DB id stored as a string in the JWT
      const candidateId = parseInt(user.jti, 10)
      const res = await getApplicationsByCandidate(candidateId)
      setApplications(res.data)
    } catch {
      setError('Could not load your applications. Please try again later.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <div className="section-header">
        <div>
          <h1 className="page-title">My Applications</h1>
          <p className="page-subtitle">Track the status of your job applications</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="flex-center" style={{ padding: '3rem' }}>
          <span className="spinner spinner-dark" style={{ width: '2rem', height: '2rem', borderWidth: '3px' }} />
        </div>
      ) : applications.length === 0 ? (
        // Friendly empty state — no need to show a blank table
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ fontSize: '1.1rem', marginBottom: '0.5rem' }}>
            You haven&apos;t applied to any vacancies yet.
          </p>
          <p className="text-muted">
            Browse the <a href="/vacancies">Vacancies</a> page to find open positions.
          </p>
        </div>
      ) : (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Vacancy</th>
                <th>Category</th>
                <th>Applied on</th>
                <th>Status</th>
                <th>Observations</th>
              </tr>
            </thead>
            <tbody>
              {applications.map((app) => (
                <tr key={app.id}>
                  <td>{app.id}</td>
                  <td style={{ fontWeight: 500 }}>{app.vacancy?.title ?? '—'}</td>
                  <td>
                    <span style={{ fontSize: '0.8rem', color: 'var(--muted)' }}>
                      {app.vacancy?.category?.replace(/_/g, ' ') ?? '—'}
                    </span>
                  </td>
                  <td>{app.applicationDate}</td>
                  <td>
                    <span className={`badge ${STATUS_BADGE[app.status] ?? 'badge-gray'}`}>
                      {app.status?.replace(/_/g, ' ')}
                    </span>
                  </td>
                  {/* Observations are optional — show a dash when absent */}
                  <td className="text-muted" style={{ fontSize: '0.85rem' }}>
                    {app.observations ?? '—'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
