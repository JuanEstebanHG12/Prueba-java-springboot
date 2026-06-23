import { useEffect, useState } from 'react'
import { getAllApplications, changeApplicationStatus } from '../../api/applicationsApi'
import { createInterview } from '../../api/interviewsApi'
import { getErrorMessage } from '../../api/client'

// All statuses the admin can move an application to
const APPLICATION_STATUSES = [
  'PENDING',
  'UNDER_REVIEW',
  'INTERVIEW_SCHEDULED',
  'ACCEPTED',
  'REJECTED',
  'WITHDRAWN',
]

const STATUS_BADGE = {
  ACCEPTED:             'badge-green',
  INTERVIEW_SCHEDULED:  'badge-blue',
  UNDER_REVIEW:         'badge-blue',
  REJECTED:             'badge-red',
  WITHDRAWN:            'badge-red',
  PENDING:              'badge-gray',
}

const INTERVIEW_TYPES = ['VIRTUAL', 'IN_PERSON', 'PHONE']

// Build the minimum datetime string for the datetime-local input (no past dates)
function todayISO() {
  const now = new Date()
  // Slice off seconds and timezone so the value fits datetime-local format
  return now.toISOString().slice(0, 16)
}

export default function ApplicationsPage() {
  const [applications, setApplications] = useState([])
  const [loading, setLoading]           = useState(true)
  const [error, setError]               = useState('')

  // Track a pending status change per row without a round-trip on every keystroke
  const [statusMap, setStatusMap] = useState({})
  const [savingId, setSavingId]   = useState(null)
  const [rowError, setRowError]   = useState({})

  // Interview modal state
  const [showModal, setShowModal]         = useState(false)
  const [modalApp, setModalApp]           = useState(null)
  const [interviewForm, setInterviewForm] = useState({ scheduledDate: '', type: 'VIRTUAL', notes: '' })
  const [modalError, setModalError]       = useState('')
  const [scheduling, setScheduling]       = useState(false)
  const [scheduleSuccess, setScheduleSuccess] = useState(false)

  useEffect(() => {
    fetchApplications()
  }, [])

  async function fetchApplications() {
    setLoading(true)
    setError('')
    try {
      const res = await getAllApplications()
      const data = res.data
      setApplications(data)
      // Pre-fill the status map with each app's current status so the select shows the right value
      const initial = {}
      data.forEach((a) => { initial[a.id] = a.status })
      setStatusMap(initial)
    } catch {
      setError('Could not load applications. Make sure the backend is running.')
    } finally {
      setLoading(false)
    }
  }

  async function handleSaveStatus(appId) {
    setSavingId(appId)
    setRowError((prev) => ({ ...prev, [appId]: '' }))
    try {
      await changeApplicationStatus(appId, { status: statusMap[appId] })
      // Refresh so the badge reflects the saved value immediately
      await fetchApplications()
    } catch (err) {
      const msg = getErrorMessage(err, 'Could not update status.')
      setRowError((prev) => ({ ...prev, [appId]: msg }))
    } finally {
      setSavingId(null)
    }
  }

  function openInterviewModal(app) {
    setModalApp(app)
    setInterviewForm({ scheduledDate: todayISO(), type: 'VIRTUAL', notes: '' })
    setModalError('')
    setScheduleSuccess(false)
    setShowModal(true)
  }

  function closeModal() {
    setShowModal(false)
    setModalApp(null)
    setModalError('')
    setScheduleSuccess(false)
  }

  function handleInterviewChange(e) {
    setInterviewForm({ ...interviewForm, [e.target.name]: e.target.value })
  }

  async function handleScheduleInterview(e) {
    e.preventDefault()
    setModalError('')
    setScheduling(true)

    try {
      await createInterview({
        applicationId: modalApp.id,
        // Backend expects ISO datetime string — datetime-local gives us "YYYY-MM-DDTHH:mm"
        scheduledDate: interviewForm.scheduledDate,
        type: interviewForm.type,
        notes: interviewForm.notes.trim() || null,
      })
      setScheduleSuccess(true)
    } catch (err) {
      const msg = getErrorMessage(err, 'Could not schedule interview. Please try again.')
      setModalError(msg)
    } finally {
      setScheduling(false)
    }
  }

  return (
    <div className="page">
      <div className="section-header">
        <div>
          <h1 className="page-title">Applications</h1>
          <p className="page-subtitle">Review and manage candidate applications</p>
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
                <th>Candidate</th>
                <th>Vacancy</th>
                <th>Applied on</th>
                <th>Status</th>
                <th>Change status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {applications.length === 0 ? (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: '2rem', color: 'var(--muted)' }}>
                    No applications yet.
                  </td>
                </tr>
              ) : (
                applications.map((app) => (
                  <tr key={app.id}>
                    <td>{app.id}</td>
                    <td>
                      <span style={{ fontWeight: 500 }}>
                        {app.candidate?.name} {app.candidate?.lastName}
                      </span>
                      <br />
                      <span className="text-muted" style={{ fontSize: '0.78rem' }}>
                        {app.candidate?.email}
                      </span>
                    </td>
                    <td style={{ fontWeight: 500 }}>{app.vacancy?.title ?? '—'}</td>
                    <td>{app.applicationDate}</td>
                    <td>
                      <span className={`badge ${STATUS_BADGE[app.status] ?? 'badge-gray'}`}>
                        {app.status?.replace(/_/g, ' ')}
                      </span>
                    </td>
                    <td>
                      {/* Inline status changer — select + save button per row */}
                      <div className="flex" style={{ gap: '0.4rem', alignItems: 'center' }}>
                        <select
                          className="form-select"
                          style={{ padding: '0.3rem 0.5rem', fontSize: '0.82rem' }}
                          value={statusMap[app.id] ?? app.status}
                          onChange={(e) =>
                            setStatusMap((prev) => ({ ...prev, [app.id]: e.target.value }))
                          }
                        >
                          {APPLICATION_STATUSES.map((s) => (
                            <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>
                          ))}
                        </select>
                        <button
                          className="btn btn-primary btn-sm"
                          disabled={savingId === app.id}
                          onClick={() => handleSaveStatus(app.id)}
                        >
                          {savingId === app.id ? <span className="spinner" /> : 'Save'}
                        </button>
                      </div>
                      {/* Show per-row error inline so it doesn't block the rest of the table */}
                      {rowError[app.id] && (
                        <p style={{ color: 'var(--danger)', fontSize: '0.78rem', marginTop: '0.25rem' }}>
                          {rowError[app.id]}
                        </p>
                      )}
                    </td>
                    <td>
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => openInterviewModal(app)}
                      >
                        Schedule interview
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Schedule interview modal */}
      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">
                Schedule interview — App #{modalApp?.id}
              </h2>
              <button className="modal-close" onClick={closeModal}>✕</button>
            </div>

            <div className="modal-body">
              {scheduleSuccess ? (
                <>
                  <div className="alert alert-success">Interview scheduled successfully!</div>
                  <div className="flex" style={{ justifyContent: 'flex-end', marginTop: '1rem' }}>
                    <button className="btn btn-primary" onClick={closeModal}>Close</button>
                  </div>
                </>
              ) : (
                <form onSubmit={handleScheduleInterview}>
                  {modalError && <div className="alert alert-error">{modalError}</div>}

                  <div className="form-group">
                    <label className="form-label">Scheduled date & time *</label>
                    <input
                      type="datetime-local"
                      name="scheduledDate"
                      className="form-input"
                      value={interviewForm.scheduledDate}
                      min={todayISO()}
                      onChange={handleInterviewChange}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Interview type *</label>
                    <select
                      name="type"
                      className="form-select"
                      value={interviewForm.type}
                      onChange={handleInterviewChange}
                      required
                    >
                      {INTERVIEW_TYPES.map((t) => (
                        <option key={t} value={t}>{t.replace(/_/g, ' ')}</option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label">Notes (optional)</label>
                    <textarea
                      name="notes"
                      className="form-input"
                      placeholder="Any preparation notes or instructions..."
                      value={interviewForm.notes}
                      onChange={handleInterviewChange}
                      rows={3}
                      style={{ resize: 'vertical' }}
                    />
                  </div>

                  <div className="flex" style={{ gap: '0.75rem', justifyContent: 'flex-end', marginTop: '1rem' }}>
                    <button type="button" className="btn btn-secondary" onClick={closeModal}>
                      Cancel
                    </button>
                    <button type="submit" className="btn btn-primary" disabled={scheduling}>
                      {scheduling ? <span className="spinner" /> : 'Schedule'}
                    </button>
                  </div>
                </form>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
