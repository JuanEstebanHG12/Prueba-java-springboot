import { useEffect, useState } from 'react'
import { getAllInterviews, changeInterviewStatus } from '../../api/interviewsApi'
import { getErrorMessage } from '../../api/client'

const INTERVIEW_STATUSES = ['SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED']

// Colour-code interview statuses so admins can scan the table quickly
const STATUS_BADGE = {
  SCHEDULED:   'badge-blue',
  COMPLETED:   'badge-green',
  CANCELLED:   'badge-red',
  RESCHEDULED: 'badge-yellow',
}

export default function InterviewsPage() {
  const [interviews, setInterviews] = useState([])
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState('')

  // Per-row status select + save state (same pattern as ApplicationsPage)
  const [statusMap, setStatusMap] = useState({})
  const [savingId, setSavingId]   = useState(null)
  const [rowError, setRowError]   = useState({})

  useEffect(() => {
    fetchInterviews()
  }, [])

  async function fetchInterviews() {
    setLoading(true)
    setError('')
    try {
      const res = await getAllInterviews()
      const data = res.data
      setInterviews(data)
      // Pre-seed so every select shows the correct current status on first render
      const initial = {}
      data.forEach((i) => { initial[i.id] = i.status })
      setStatusMap(initial)
    } catch {
      setError('Could not load interviews. Make sure the backend is running.')
    } finally {
      setLoading(false)
    }
  }

  async function handleSaveStatus(interviewId) {
    setSavingId(interviewId)
    setRowError((prev) => ({ ...prev, [interviewId]: '' }))
    try {
      await changeInterviewStatus(interviewId, { status: statusMap[interviewId] })
      await fetchInterviews()
    } catch (err) {
      const msg = getErrorMessage(err, 'Could not update status.')
      setRowError((prev) => ({ ...prev, [interviewId]: msg }))
    } finally {
      setSavingId(null)
    }
  }

  // Format ISO datetime to something more human-friendly
  function formatDate(iso) {
    if (!iso) return '—'
    return iso.replace('T', ' ').slice(0, 16)
  }

  return (
    <div className="page">
      <div className="section-header">
        <div>
          <h1 className="page-title">Interviews</h1>
          <p className="page-subtitle">Manage all scheduled interviews</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="flex-center" style={{ padding: '3rem' }}>
          <span className="spinner spinner-dark" style={{ width: '2rem', height: '2rem', borderWidth: '3px' }} />
        </div>
      ) : (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>App #</th>
                <th>Candidate</th>
                <th>Vacancy</th>
                <th>Scheduled</th>
                <th>Type</th>
                <th>Status</th>
                <th>Change status</th>
              </tr>
            </thead>
            <tbody>
              {interviews.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '2rem', color: 'var(--muted)' }}>
                    No interviews scheduled yet.
                  </td>
                </tr>
              ) : (
                interviews.map((iv) => (
                  <tr key={iv.id}>
                    <td>{iv.id}</td>
                    {/* application is a nested object from the backend response */}
                    <td>{iv.application?.id ?? '—'}</td>
                    <td>
                      <span style={{ fontWeight: 500 }}>
                        {iv.application?.candidate?.name} {iv.application?.candidate?.lastName}
                      </span>
                    </td>
                    <td>{iv.application?.vacancy?.title ?? '—'}</td>
                    <td>{formatDate(iv.scheduledDate)}</td>
                    <td>{iv.type}</td>
                    <td>
                      <span className={`badge ${STATUS_BADGE[iv.status] ?? 'badge-gray'}`}>
                        {iv.status?.replace(/_/g, ' ')}
                      </span>
                    </td>
                    <td>
                      <div className="flex" style={{ gap: '0.4rem', alignItems: 'center' }}>
                        <select
                          className="form-select"
                          style={{ padding: '0.3rem 0.5rem', fontSize: '0.82rem' }}
                          value={statusMap[iv.id] ?? iv.status}
                          onChange={(e) =>
                            setStatusMap((prev) => ({ ...prev, [iv.id]: e.target.value }))
                          }
                        >
                          {INTERVIEW_STATUSES.map((s) => (
                            <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>
                          ))}
                        </select>
                        <button
                          className="btn btn-primary btn-sm"
                          disabled={savingId === iv.id}
                          onClick={() => handleSaveStatus(iv.id)}
                        >
                          {savingId === iv.id ? <span className="spinner" /> : 'Save'}
                        </button>
                      </div>
                      {rowError[iv.id] && (
                        <p style={{ color: 'var(--danger)', fontSize: '0.78rem', marginTop: '0.25rem' }}>
                          {rowError[iv.id]}
                        </p>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
