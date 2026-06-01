import { useEffect, useState } from 'react'
import './App.css'

function App() {
  const [health, setHealth] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetch('/api/health')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Backend respondeu com erro')
        }

        return response.json()
      })
      .then((data) => {
        setHealth(data)
        setError(null)
      })
      .catch(() => {
        setError('Não foi possível ligar ao backend.')
        setHealth(null)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [])

  return (
    <main className="app-container">
      <section className="hero-card">
        <p className="eyebrow">LurePilot AI</p>

        <h1>Local AI Fishing Copilot</h1>

        <p className="description">
          Plataforma para planear sessões de pesca, gerir amostras, registar resultados
          e gerar recomendações práticas com IA local.
        </p>

        <div className="status-card">
          <h2>Backend status</h2>

          {loading && <p className="muted">A verificar ligação ao backend...</p>}

          {error && <p className="error">{error}</p>}

          {health && (
            <div className="status-content">
              <p>
                <strong>Status:</strong> {health.status}
              </p>
              <p>
                <strong>Service:</strong> {health.service}
              </p>
              <p>
                <strong>Timestamp:</strong> {health.timestamp}
              </p>
            </div>
          )}
        </div>
      </section>
    </main>
  )
}

export default App