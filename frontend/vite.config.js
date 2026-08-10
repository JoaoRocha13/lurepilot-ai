/* global process */
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'node:fs'
import path from 'node:path'

const useHttps = process.env.VITE_HTTPS === 'true'
const httpsKeyPath = process.env.VITE_HTTPS_KEY || path.resolve(process.cwd(), '.certs/lurepilot-key.pem')
const httpsCertPath = process.env.VITE_HTTPS_CERT || path.resolve(process.cwd(), '.certs/lurepilot-cert.pem')

function localHttpsConfig() {
  if (!useHttps) {
    return undefined
  }

  if (!fs.existsSync(httpsKeyPath) || !fs.existsSync(httpsCertPath)) {
    throw new Error(`HTTPS is enabled, but the local certificate files were not found at ${httpsKeyPath} and ${httpsCertPath}`)
  }

  return {
    key: fs.readFileSync(httpsKeyPath),
    cert: fs.readFileSync(httpsCertPath),
  }
}

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: [{ find: 'react-native', replacement: 'react-native-web' }],
  },
  server: {
    host: process.env.VITE_HOST || '0.0.0.0',
    port: Number(process.env.VITE_PORT || 5173),
    strictPort: true,
    https: localHttpsConfig(),
    proxy: {
      '/api': {
        target: process.env.VITE_BACKEND_URL || 'http://localhost:8080',
        changeOrigin: true,
      },
      '/uploads': {
        target: process.env.VITE_BACKEND_URL || 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
