import { useEffect, useState } from 'react'
import { getPublicVacancies } from '../api/vacanciesApi'
import { applyToVacancy } from '../api/applicationsApi'
import { getErrorMessage } from '../api/client'
import { useAuth } from '../context/AuthContext'

// Status badge map — same convention used across the whole app
const STATUS_BADGE = {
  OPEN:        'badge-green',
  IN_PROGRESS: 'badge-blue',
  CLOSED:      'badge-red',
  CANCELLED:   'badge-gray',
}

// Only OPEN vacancies make sense to apply to; others are already filled or cancelled
const APPLY_FILTER = 'OPEN'

// Filter options shown in the top bar
const FILTER_OPTIONS = ['All', 'OPEN', 'CLOSED']

export default function VacanciesListPage() {
  const { role, user } = useAuth()
  const isCandidate = role === 'CANDIDATE'

  const [vacancies, setVacancies]   = useState([])
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState('')
  const [filter, setFilter]         = useState('All')

  // Modal state — tracks which vacancy the candidate is applying to
  const [showModal, setShowModal]   = useState(false)
  const [targetVacancy, setTarget]  = useState(null)
  const [observations, setObs]      = useState('')
  const [applying, setApplying]     = useState(false)
  const [applyError, setApplyError] = useState('')
  const [applySuccess, setApplySuccess] = useState(false)

  useEffect(() => {
    fetchVacancies()
  }, [])

  async function fetchVacancies() {
    setLoading(true)
    setError('')
    try {
      const res = await getPublicVacancies()
      setVacancies(res.data)
    } catch {
      setError('Could not load vacancies. Make sure the backend is running.')
    } finally {
      setLoading(false)
    }
  }

  // Client-side filter — no round-trip needed for such a simple case
  const displayed = filter === 'All'
    ? vacancies
    : vacancies.filter((v) => v.status === filter)

  function openApplyModal(vacancy) {
    setTarget(vacancy)
    setObs('')
    setApplyError('')
    setApplySuccess(false)
    setShowModal(true)
  }

  function closeModal() {
    setShowModal(false)
    setTarget(null)
    setObs('')
    setApplyError('')
    setApplySuccess(false)
  }

  async function handleApply(e) {
    e.preventDefault()
    setApplyError('')
    setApplying(true)

    try {
      await applyToVacancy({
        // jti holds the user's numeric DB id as a string — parse it before sending
        candidateId: parseInt(user.jti, 10),
        vacancyId: targetVacancy.id,
        observations: observations.trim() || null,
      })
      setApplySuccess(true)
    } catch (err) {
      // The backend returns an error message when the candidate already applied or other issues
      const msg = getErrorMessage(err, 'Could not submit application. Please try again.')
      setApplyError(msg)
    } finally {
      setApplying(false)
    }
  }

  return (
    <div className="page">
      <div className="section-header">
        <div>
          <h1 className="page-title">Job Vacancies</h1>
          <p className="page-subtitle">Browse open positions</p>
        </div>

        {/* Filter bar — candidate and other roles both use this */}
        <div className="flex" style={{ gap: '0.5rem' }}>
          {FILTER_OPTIONS.map((opt) => (
            <button
              key={opt}
              className={`btn btn-sm ${filter === opt ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setFilter(opt)}
            >
              {opt}
            </button>
          ))}
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
                <th>Title</th>
                <th>Category</th>
                <th>Mode</th>
                <th>Salary</th>
                <th>Status</th>
                <th>Published</th>
                {/* Only show the Apply column to candidates */}
                {isCandidate && <th>Apply</th>}
              </tr>
            </thead>
            <tbody>
              {displayed.length === 0 ? (
                <tr>
                  <td
                    colSpan={isCandidate ? 8 : 7}
                    style={{ textAlign: 'center', padding: '2rem', color: 'var(--muted)' }}
                  >
                    No vacancies match the selected filter.
                  </td>
                </tr>
              ) : (
                displayed.map((v) => (
                  <tr key={v.id}>
                    <td>{v.id}</td>
                    <td style={{ fontWeight: 500 }}>{v.title}</td>
                    <td>
                      <span style={{ fontSize: '0.8rem', color: 'var(--muted)' }}>
                        {v.category.replace(/_/g, ' ')}
                      </span>
                    </td>
                    <td>{v.mode}</td>
                    <td>{v.salary ? `$${Number(v.salary).toLocaleString()}` : '—'}</td>
                    <td>
                      <span className={`badge ${STATUS_BADGE[v.status] ?? 'badge-gray'}`}>
                        {v.status.replace(/_/g, ' ')}
                      </span>
                    </td>
                    <td>{v.publishDate}</td>
                    {isCandidate && (
                      <td>
                        {/* Only OPEN vacancies can receive new applications */}
                        {v.status === APPLY_FILTER ? (
                          <button
                            className="btn btn-primary btn-sm"
                            onClick={() => openApplyModal(v)}
                          >
                            Apply
                          </button>
                        ) : (
                          <span className="text-muted" style={{ fontSize: '0.8rem' }}>—</span>
                        )}
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Application modal */}
      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">Apply — {targetVacancy?.title}</h2>
              <button className="modal-close" onClick={closeModal}>✕</button>
            </div>

            <div className="modal-body">
              {/* Success state: show confirmation and let them close */}
              {applySuccess ? (
                <>
                  <div className="alert alert-success">Application submitted successfully!</div>
                  <div className="flex" style={{ justifyContent: 'flex-end', marginTop: '1rem' }}>
                    <button className="btn btn-primary" onClick={closeModal}>Close</button>
                  </div>
                </>
              ) : (
                <form onSubmit={handleApply}>
                  {applyError && <div className="alert alert-error">{applyError}</div>}

                  <div className="form-group">
                    <label className="form-label">Observations (optional)</label>
                    <textarea
                      className="form-input"
                      placeholder="Add a short message or cover note..."
                      value={observations}
                      onChange={(e) => setObs(e.target.value)}
                      rows={4}
                      style={{ resize: 'vertical' }}
                    />
                  </div>

                  <div className="flex" style={{ gap: '0.75rem', justifyContent: 'flex-end', marginTop: '1rem' }}>
                    <button type="button" className="btn btn-secondary" onClick={closeModal}>
                      Cancel
                    </button>
                    <button type="submit" className="btn btn-primary" disabled={applying}>
                      {applying ? <span className="spinner" /> : 'Submit application'}
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
