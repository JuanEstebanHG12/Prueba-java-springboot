import { useEffect, useState } from 'react'
import { getAllVacancies, createVacancy, updateVacancy, changeVacancyStatus } from '../../api/vacanciesApi'
import { getErrorMessage } from '../../api/client'
import { useAuth } from '../../context/AuthContext'

const JOB_CATEGORIES = [
  'SOFTWARE_DEVELOPMENT', 'DATA_ANALYTICS', 'CYBERSECURITY', 'DEVOPS',
  'QUALITY_ASSURANCE', 'UI_UX_DESIGN', 'PROJECT_MANAGEMENT', 'HUMAN_RESOURCES',
  'MARKETING', 'SALES', 'CUSTOMER_SERVICE', 'FINANCE', 'ACCOUNTING',
  'LEGAL', 'OPERATIONS', 'LOGISTICS', 'PROCUREMENT', 'EDUCATION',
  'HEALTHCARE', 'ADMINISTRATION',
]

const WORK_MODES = ['REMOTE', 'HYBRID', 'ONSITE']

const STATUS_BADGE = {
  OPEN:        'badge-green',
  IN_PROGRESS: 'badge-blue',
  CLOSED:      'badge-red',
  CANCELLED:   'badge-gray',
}

const EMPTY_FORM = {
  title: '',
  description: '',
  category: '',
  mode: '',
  salary: '',
  responsibleId: '',
}

export default function VacanciesPage() {
  const { user } = useAuth()

  const [vacancies, setVacancies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null) // null = creating, object = editing
  const [form, setForm] = useState(EMPTY_FORM)
  const [formError, setFormError] = useState('')
  const [saving, setSaving] = useState(false)

  // Per-row status change state — tracks the selected value and in-progress save per vacancy
  const [vacancyStatusMap, setVacancyStatusMap] = useState({})
  const [statusSavingId, setStatusSavingId]     = useState(null)
  const [statusRowError, setStatusRowError]     = useState({})

  useEffect(() => {
    fetchVacancies()
  }, [])

  async function fetchVacancies() {
    setLoading(true)
    setError('')
    try {
      const res = await getAllVacancies()
      const data = res.data
      setVacancies(data)
      // Pre-seed the status dropdowns so they match the current DB value on first render
      const initial = {}
      data.forEach((v) => { initial[v.id] = v.status })
      setVacancyStatusMap(initial)
    } catch (err) {
      setError('Could not load vacancies. Make sure the backend is running.')
    } finally {
      setLoading(false)
    }
  }

  async function handleSaveVacancyStatus(vacancyId) {
    setStatusSavingId(vacancyId)
    setStatusRowError((prev) => ({ ...prev, [vacancyId]: '' }))
    try {
      await changeVacancyStatus(vacancyId, { status: vacancyStatusMap[vacancyId] })
      // Refresh the full list so the badge and status map stay in sync
      await fetchVacancies()
    } catch (err) {
      const msg = getErrorMessage(err, 'Could not update status.')
      setStatusRowError((prev) => ({ ...prev, [vacancyId]: msg }))
    } finally {
      setStatusSavingId(null)
    }
  }

  function openCreate() {
    setEditing(null)
    setForm({ ...EMPTY_FORM, responsibleId: user?.jti ?? '' })
    setFormError('')
    setShowModal(true)
  }

  function openEdit(vacancy) {
    setEditing(vacancy)
    setForm({
      title: vacancy.title,
      description: vacancy.description,
      category: vacancy.category,
      mode: vacancy.mode,
      salary: vacancy.salary ?? '',
      responsibleId: vacancy.responsible?.id ?? user?.jti ?? '',
    })
    setFormError('')
    setShowModal(true)
  }

  function closeModal() {
    setShowModal(false)
    setEditing(null)
    setForm(EMPTY_FORM)
    setFormError('')
  }

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setFormError('')
    setSaving(true)

    const payload = {
      title: form.title,
      description: form.description,
      category: form.category,
      mode: form.mode,
      salary: form.salary === '' ? null : parseFloat(form.salary),
      responsibleId: parseInt(form.responsibleId, 10),
    }

    try {
      if (editing) {
        await updateVacancy(editing.id, payload)
      } else {
        await createVacancy(payload)
      }
      closeModal()
      fetchVacancies()
    } catch (err) {
      const msg = getErrorMessage(err)
      setFormError(msg)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="page">
      <div className="section-header">
        <div>
          <h1 className="page-title">Vacancies</h1>
          <p className="page-subtitle">Manage all job vacancies</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>
          + New vacancy
        </button>
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
                <th>Responsible</th>
                <th>Published</th>
                <th>Change status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {vacancies.length === 0 ? (
                <tr>
                  <td colSpan={10} style={{ textAlign: 'center', padding: '2rem', color: 'var(--muted)' }}>
                    No vacancies yet. Create the first one!
                  </td>
                </tr>
              ) : (
                vacancies.map((v) => (
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
                    <td>{v.responsible?.name ?? '—'}</td>
                    <td>{v.publishDate}</td>
                    <td>
                      {/* Inline status changer — only updates the status field, not the full vacancy */}
                      <div className="flex" style={{ gap: '0.4rem', alignItems: 'center' }}>
                        <select
                          className="form-select"
                          style={{ padding: '0.3rem 0.5rem', fontSize: '0.82rem' }}
                          value={vacancyStatusMap[v.id] ?? v.status}
                          onChange={(e) =>
                            setVacancyStatusMap((prev) => ({ ...prev, [v.id]: e.target.value }))
                          }
                        >
                          <option value="OPEN">OPEN</option>
                          <option value="IN_PROGRESS">IN PROGRESS</option>
                          <option value="CLOSED">CLOSED</option>
                          <option value="CANCELLED">CANCELLED</option>
                        </select>
                        <button
                          className="btn btn-primary btn-sm"
                          disabled={statusSavingId === v.id}
                          onClick={() => handleSaveVacancyStatus(v.id)}
                        >
                          {statusSavingId === v.id ? <span className="spinner" /> : 'Update'}
                        </button>
                      </div>
                      {statusRowError[v.id] && (
                        <p style={{ color: 'var(--danger)', fontSize: '0.78rem', marginTop: '0.25rem' }}>
                          {statusRowError[v.id]}
                        </p>
                      )}
                    </td>
                    <td>
                      <button
                        className="btn btn-ghost btn-sm"
                        onClick={() => openEdit(v)}
                      >
                        Edit
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">
                {editing ? `Edit vacancy #${editing.id}` : 'New vacancy'}
              </h2>
              <button className="modal-close" onClick={closeModal}>✕</button>
            </div>

            <div className="modal-body">
              {formError && <div className="alert alert-error">{formError}</div>}

              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label className="form-label">Title *</label>
                  <input
                    type="text"
                    name="title"
                    className="form-input"
                    placeholder="e.g. Senior Backend Developer"
                    value={form.title}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Description * (10–100 chars)</label>
                  <textarea
                    name="description"
                    className="form-input"
                    placeholder="Brief description of the role..."
                    value={form.description}
                    onChange={handleChange}
                    required
                    minLength={10}
                    maxLength={100}
                    rows={3}
                    style={{ resize: 'vertical' }}
                  />
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="form-label">Category *</label>
                    <select
                      name="category"
                      className="form-select"
                      value={form.category}
                      onChange={handleChange}
                      required
                    >
                      <option value="">Select category</option>
                      {JOB_CATEGORIES.map((c) => (
                        <option key={c} value={c}>
                          {c.replace(/_/g, ' ')}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label">Work mode *</label>
                    <select
                      name="mode"
                      className="form-select"
                      value={form.mode}
                      onChange={handleChange}
                      required
                    >
                      <option value="">Select mode</option>
                      {WORK_MODES.map((m) => (
                        <option key={m} value={m}>{m}</option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="form-label">Salary (optional)</label>
                    <input
                      type="number"
                      name="salary"
                      className="form-input"
                      placeholder="e.g. 3500"
                      value={form.salary}
                      onChange={handleChange}
                      min={0}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Responsible user ID *</label>
                    <input
                      type="number"
                      name="responsibleId"
                      className="form-input"
                      placeholder="Admin user ID"
                      value={form.responsibleId}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="flex" style={{ gap: '0.75rem', justifyContent: 'flex-end', marginTop: '1rem' }}>
                  <button type="button" className="btn btn-secondary" onClick={closeModal}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? <span className="spinner" /> : editing ? 'Save changes' : 'Create vacancy'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
