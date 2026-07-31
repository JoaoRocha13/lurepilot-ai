import { useEffect, useMemo, useState } from 'react'
import {
  ActivityIndicator,
  Image,
  ImageBackground,
  Pressable,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  View,
  useWindowDimensions,
} from 'react-native'

import appIcon from '../assets/images/brand/app-icon.png'
import blackBass from '../assets/images/fish/freshwater/black-bass.png'
import dashboardIcon from '../assets/images/ui/dashboard-icon.png'
import fishingPlanIcon from '../assets/images/ui/fishingplan-icon.png'
import galleryIcon from '../assets/images/ui/gallery-icon.png'
import libraryIcon from '../assets/images/ui/library-icon.png'
import lureBoxIcon from '../assets/images/ui/lurebox-icon.png'
import profileIcon from '../assets/images/ui/profile-icon.png'
import sessionIcon from '../assets/images/ui/session-icon.png'
import damSpot from '../assets/images/spots/dam.png'
import lakeSpot from '../assets/images/spots/lake.png'
import spotsIcon from '../assets/images/ui/spots-icon.png'
import clearSky from '../assets/images/weather/clear-sky.png'
import spinnerbait from '../assets/images/lures/spinnerbait.png'

const menuItems = [
  { id: 'dashboard', image: dashboardIcon, iconScale: 1.55 },
  { id: 'gallery', image: galleryIcon, iconScale: 1.55 },
  { id: 'spots', image: spotsIcon, iconScale: 1.55 },
  { id: 'plans', image: fishingPlanIcon, iconScale: 1.55 },
  { id: 'session', image: sessionIcon, iconScale: 1.55 },
  { id: 'lureBox', image: lureBoxIcon, iconScale: 1.55 },
  { id: 'library', image: libraryIcon, iconScale: 1.45 },
  { id: 'profile', image: profileIcon, iconScale: 1.7 },
]

const featureImages = {
  dashboard: damSpot,
  gallery: galleryIcon,
  spots: lakeSpot,
  plans: fishingPlanIcon,
  session: sessionIcon,
  lureBox: lureBoxIcon,
  library: libraryIcon,
  profile: profileIcon,
}

const translations = {
  pt: {
    brandSubline: 'Planear. Pescar. Aprender.',
    today: 'Hoje',
    backendOnline: 'Backend online',
    backendOffline: 'Backend offline',
    backendUnavailable: 'Sem ligação ao backend. Estou a mostrar uma vista de exemplo.',
    languageLabel: 'Idioma',
    areaLabel: 'Área',
    loading: 'A carregar dados...',
    undefinedDate: 'data por definir',
    at: 'às',
    menu: {
      dashboard: 'Dashboard',
      gallery: 'Galeria',
      spots: 'Spots',
      plans: 'Planos',
      session: 'Sessão',
      lureBox: 'Lure Box',
      library: 'Biblioteca',
      profile: 'Perfil',
    },
    sections: {
      dashboard: {
        title: 'Dashboard',
        subtitle: 'A tua base de comando para a próxima pesca.',
      },
      gallery: {
        title: 'Galeria de capturas',
        subtitle: 'Histórico visual de peixes, fotos e sessões.',
      },
      spots: {
        title: 'Spots',
        subtitle: 'Locais, água, espécies e notas de terreno.',
      },
      plans: {
        title: 'Planos',
        subtitle: 'Planeamento A/B/C com spot, weather e lures.',
      },
      session: {
        title: 'Sessão ativa',
        subtitle: 'Registo rápido do que acontece durante a pesca.',
      },
      lureBox: {
        title: 'Lure Box',
        subtitle: 'O teu inventário pessoal de lures.',
      },
      library: {
        title: 'Biblioteca',
        subtitle: 'Espécies e lures com contexto prático.',
      },
      profile: {
        title: 'Perfil',
        subtitle: 'Preferências e histórico pessoal.',
      },
    },
    dashboard: {
      focusLabel: 'Próxima decisão',
      focusFallbackTitle: 'Prepara uma sessão com mais intenção',
      focusFallbackText: 'Cruza spot, condições, histórico e lures antes de sair de casa.',
      speciesFallback: 'Espécie por definir',
      activeChip: 'ativa(s)',
      successChip: 'sucesso',
      pendingChip: 'por avaliar',
      createPlan: 'Criar plano',
      createPlanDetail: 'Spot + weather + lures',
      activeSession: 'Sessão ativa',
      activeSessionDetail: 'em curso',
      registerCatch: 'Registar captura',
      historyFish: 'peixe(s) no histórico',
      spots: 'Spots',
      plans: 'Planos',
      sessions: 'Sessões',
      lures: 'Lures',
      bestLure: 'Melhor lure recente',
      noPattern: 'Ainda sem padrão',
      bestLureFallback: 'Surge depois de algumas sessões.',
      uses: 'uso(s)',
      weather: 'Weather relevante',
      noSnapshot: 'Sem snapshot',
      wind: 'vento',
      weatherFallback: 'Liga um snapshot IPMA a um plano.',
      latestResult: 'Último resultado',
      noResults: 'Sem resultados ainda',
      resultFallback: 'Quando terminares uma sessão, aparece aqui.',
      fishCount: 'peixe(s)',
      latestCatch: 'Última captura',
      emptyGallery: 'Galeria vazia',
      inWord: 'em',
      unnamedSpot: 'spot sem nome',
      catchFallback: 'A galeria ganha vida quando adicionares fotos.',
    },
  },
  en: {
    brandSubline: 'Plan. Fish. Learn.',
    today: 'Today',
    backendOnline: 'Backend online',
    backendOffline: 'Backend offline',
    backendUnavailable: 'No backend connection. Showing a sample view.',
    languageLabel: 'Language',
    areaLabel: 'Area',
    loading: 'Loading data...',
    undefinedDate: 'date not set',
    at: 'at',
    menu: {
      dashboard: 'Dashboard',
      gallery: 'Gallery',
      spots: 'Spots',
      plans: 'Plans',
      session: 'Session',
      lureBox: 'Lure Box',
      library: 'Library',
      profile: 'Profile',
    },
    sections: {
      dashboard: {
        title: 'Dashboard',
        subtitle: 'Your command base for the next fishing session.',
      },
      gallery: {
        title: 'Catch gallery',
        subtitle: 'Visual history of fish, photos and sessions.',
      },
      spots: {
        title: 'Spots',
        subtitle: 'Locations, water, species and field notes.',
      },
      plans: {
        title: 'Plans',
        subtitle: 'A/B/C planning with spot, weather and lures.',
      },
      session: {
        title: 'Active session',
        subtitle: 'Fast logging for what happens during fishing.',
      },
      lureBox: {
        title: 'Lure Box',
        subtitle: 'Your personal lure inventory.',
      },
      library: {
        title: 'Library',
        subtitle: 'Species and lures with practical context.',
      },
      profile: {
        title: 'Profile',
        subtitle: 'Preferences and personal history.',
      },
    },
    dashboard: {
      focusLabel: 'Next decision',
      focusFallbackTitle: 'Prepare a more intentional session',
      focusFallbackText: 'Cross spot, conditions, history and lures before leaving home.',
      speciesFallback: 'Species not set',
      activeChip: 'active',
      successChip: 'success',
      pendingChip: 'to review',
      createPlan: 'Create plan',
      createPlanDetail: 'Spot + weather + lures',
      activeSession: 'Active session',
      activeSessionDetail: 'in progress',
      registerCatch: 'Log catch',
      historyFish: 'fish in history',
      spots: 'Spots',
      plans: 'Plans',
      sessions: 'Sessions',
      lures: 'Lures',
      bestLure: 'Best recent lure',
      noPattern: 'No pattern yet',
      bestLureFallback: 'Appears after a few sessions.',
      uses: 'use(s)',
      weather: 'Relevant weather',
      noSnapshot: 'No snapshot',
      wind: 'wind',
      weatherFallback: 'Attach an IPMA snapshot to a plan.',
      latestResult: 'Latest result',
      noResults: 'No results yet',
      resultFallback: 'When you finish a session, it appears here.',
      fishCount: 'fish',
      latestCatch: 'Latest catch',
      emptyGallery: 'Empty gallery',
      inWord: 'at',
      unnamedSpot: 'unnamed spot',
      catchFallback: 'The gallery comes alive when you add photos.',
    },
  },
}

const fallbackDashboard = {
  totalSpots: 0,
  totalFishSpecies: 0,
  totalLures: 0,
  totalLureLibraryItems: 0,
  totalPlans: 0,
  totalSessions: 0,
  finishedSessions: 0,
  successfulSessions: 0,
  successRate: 0,
  totalCatchRecords: 0,
  totalFishCaught: 0,
  nextPlannedSession: null,
  bestRecentLure: null,
  relevantWeatherSnapshot: null,
  pendingRecommendationEvaluations: 0,
  upcomingPlans: [],
  activeSessions: [],
  recentResults: [],
  recentSessions: [],
  recentCatches: [],
  pendingRecommendations: [],
  recentWeatherSnapshots: [],
}

function App() {
  const { width } = useWindowDimensions()
  const compact = width < 940
  const [activeSection, setActiveSection] = useState('dashboard')
  const [language, setLanguage] = useState('pt')
  const [health, setHealth] = useState(null)
  const [dashboard, setDashboard] = useState(fallbackDashboard)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const copy = translations[language]

  useEffect(() => {
    let ignore = false

    async function loadInitialData() {
      setLoading(true)
      try {
        const [healthResponse, dashboardResponse] = await Promise.all([
          fetch('/api/health'),
          fetch('/api/dashboard'),
        ])

        if (!healthResponse.ok || !dashboardResponse.ok) {
          throw new Error('Backend unavailable')
        }

        const [healthData, dashboardData] = await Promise.all([
          healthResponse.json(),
          dashboardResponse.json(),
        ])

        if (!ignore) {
          setHealth(healthData)
          setDashboard({ ...fallbackDashboard, ...dashboardData })
          setError(null)
        }
      } catch {
        if (!ignore) {
          setHealth(null)
          setDashboard(fallbackDashboard)
          setError(true)
        }
      } finally {
        if (!ignore) {
          setLoading(false)
        }
      }
    }

    loadInitialData()

    return () => {
      ignore = true
    }
  }, [])

  const activeCopy = useMemo(
    () => ({
      ...copy.sections[activeSection],
      image: featureImages[activeSection],
    }),
    [activeSection, copy],
  )

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={[styles.shell, compact && styles.shellCompact]}>
        <View style={[styles.sidebar, compact && styles.sidebarCompact]}>
          <View style={styles.brandBlock}>
            <View style={styles.brandIconFrame}>
              <Image source={{ uri: appIcon }} style={styles.brandIcon} resizeMode="cover" />
            </View>
            <View style={styles.brandTextBlock}>
              <Text style={styles.brandName}>LurePilot AI</Text>
              <Text style={styles.brandSubline}>{copy.brandSubline}</Text>
            </View>
          </View>

          <ScrollView
            horizontal={compact}
            showsHorizontalScrollIndicator={false}
            style={compact ? styles.menuScrollCompact : undefined}
            contentContainerStyle={[styles.menuList, compact && styles.menuListCompact]}
          >
            {menuItems.map((item) => {
              const selected = activeSection === item.id

              return (
                <Pressable
                  key={item.id}
                  accessibilityRole="button"
                  accessibilityLabel={copy.menu[item.id]}
                  onPress={() => setActiveSection(item.id)}
                  style={[styles.menuItem, selected && styles.menuItemSelected]}
                >
                  <View style={styles.menuIconCrop}>
                    <Image
                      source={{ uri: item.image }}
                      style={[styles.menuIcon, { transform: [{ scale: item.iconScale }] }]}
                      resizeMode="cover"
                    />
                  </View>
                  <Text style={[styles.menuText, selected && styles.menuTextSelected]}>{copy.menu[item.id]}</Text>
                </Pressable>
              )
            })}
          </ScrollView>

          <View style={[styles.sidebarBottom, compact && styles.sidebarBottomCompact]}>
            {!compact && (
              <View style={styles.sidebarStatus}>
                <View style={[styles.statusDot, health ? styles.statusDotOk : styles.statusDotOff]} />
                <Text style={styles.sidebarStatusText}>
                  {health ? copy.backendOnline : copy.backendOffline}
                </Text>
              </View>
            )}

            <View style={styles.languageSwitch} accessibilityLabel={copy.languageLabel}>
              {['pt', 'en'].map((option) => {
                const selected = language === option

                return (
                  <Pressable
                    key={option}
                    accessibilityRole="button"
                    accessibilityLabel={option === 'pt' ? 'Português' : 'English'}
                    onPress={() => setLanguage(option)}
                    style={[styles.languageOption, selected && styles.languageOptionSelected]}
                  >
                    <Text style={[styles.languageText, selected && styles.languageTextSelected]}>
                      {option.toUpperCase()}
                    </Text>
                  </Pressable>
                )
              })}
            </View>
          </View>
        </View>

        <ScrollView style={styles.content} contentContainerStyle={styles.contentInner}>
          <View style={[styles.topBar, compact && styles.topBarCompact]}>
            <View>
              <Text style={styles.kicker}>{copy.today}</Text>
              <Text style={styles.screenTitle}>{activeCopy.title}</Text>
              <Text style={styles.screenIntro}>{activeCopy.subtitle}</Text>
            </View>

            {compact && (
              <View style={styles.backendPill}>
                <View style={[styles.statusDot, health ? styles.statusDotOk : styles.statusDotOff]} />
                <Text style={styles.backendText}>{health ? copy.backendOnline : copy.backendOffline}</Text>
              </View>
            )}
          </View>

          {error && (
            <View style={styles.notice}>
              <Text style={styles.noticeText}>{copy.backendUnavailable}</Text>
            </View>
          )}

          {activeSection === 'dashboard' ? (
            <DashboardView
              dashboard={dashboard}
              loading={loading}
              compact={compact}
              onNavigate={setActiveSection}
              copy={copy}
            />
          ) : (
            <FeaturePreview section={activeCopy} compact={compact} copy={copy} />
          )}
        </ScrollView>
      </View>
    </SafeAreaView>
  )
}

function DashboardView({ dashboard, loading, compact, onNavigate, copy }) {
  const dashboardCopy = copy.dashboard
  const nextSession = dashboard.nextPlannedSession
  const bestLure = dashboard.bestRecentLure
  const weather = dashboard.relevantWeatherSnapshot
  const recentResult = dashboard.recentResults?.[0]
  const recentCatch = dashboard.recentCatches?.[0]

  return (
    <View style={styles.dashboardStack}>
      <ImageBackground
        source={{ uri: damSpot }}
        style={styles.focusPanel}
        imageStyle={styles.focusImage}
        resizeMode="cover"
      >
        <View style={styles.focusOverlay}>
          <View style={styles.focusCopy}>
            <Text style={styles.focusLabel}>{dashboardCopy.focusLabel}</Text>
            <Text style={styles.focusTitle}>{nextSession?.spotName || dashboardCopy.focusFallbackTitle}</Text>
            <Text style={styles.focusText}>
              {nextSession
                ? `${nextSession.targetSpecies || dashboardCopy.speciesFallback} - ${formatSchedule(
                    nextSession.date,
                    nextSession.time,
                    copy,
                  )}`
                : dashboardCopy.focusFallbackText}
            </Text>
          </View>

          <View style={styles.focusChips}>
            <InfoChip label={`${dashboard.activeSessions?.length || 0} ${dashboardCopy.activeChip}`} />
            <InfoChip label={`${Math.round(dashboard.successRate || 0)}% ${dashboardCopy.successChip}`} />
            <InfoChip label={`${dashboard.pendingRecommendationEvaluations || 0} ${dashboardCopy.pendingChip}`} />
          </View>
        </View>
      </ImageBackground>

      <View style={styles.actionGrid}>
        <MissionAction
          label={dashboardCopy.createPlan}
          detail={dashboardCopy.createPlanDetail}
          image={fishingPlanIcon}
          onPress={() => onNavigate('plans')}
        />
        <MissionAction
          label={dashboardCopy.activeSession}
          detail={`${dashboard.activeSessions?.length || 0} ${dashboardCopy.activeSessionDetail}`}
          image={sessionIcon}
          onPress={() => onNavigate('session')}
        />
        <MissionAction
          label={dashboardCopy.registerCatch}
          detail={`${dashboard.totalFishCaught || 0} ${dashboardCopy.historyFish}`}
          image={galleryIcon}
          onPress={() => onNavigate('gallery')}
        />
      </View>

      <View style={styles.signalGrid}>
        <SignalTile label={dashboardCopy.spots} value={dashboard.totalSpots || 0} />
        <SignalTile label={dashboardCopy.plans} value={dashboard.totalPlans || 0} />
        <SignalTile label={dashboardCopy.sessions} value={dashboard.totalSessions || 0} />
        <SignalTile label={dashboardCopy.lures} value={dashboard.totalLures || dashboard.totalLureLibraryItems || 0} />
      </View>

      <View style={styles.panelGrid}>
        <MetricPanel
          label={dashboardCopy.bestLure}
          title={bestLure?.lureName || dashboardCopy.noPattern}
          value={
            bestLure
              ? `${bestLure.uses} ${dashboardCopy.uses}, ${bestLure.successRate}% ${dashboardCopy.successChip}`
              : dashboardCopy.bestLureFallback
          }
          image={spinnerbait}
          compact={compact}
        />
        <MetricPanel
          label={dashboardCopy.weather}
          title={weather?.sourceLocationName || dashboardCopy.noSnapshot}
          value={
            weather
              ? `${weather.temperatureMin ?? '-'}°C / ${weather.temperatureMax ?? '-'}°C, ${dashboardCopy.wind} ${
                  weather.windDirection || '-'
                }`
              : dashboardCopy.weatherFallback
          }
          image={clearSky}
          compact={compact}
        />
      </View>

      <View style={styles.panelGrid}>
        <ListPanel
          label={dashboardCopy.latestResult}
          title={recentResult?.spotName || dashboardCopy.noResults}
          body={
            recentResult
              ? `${recentResult.targetSpecies || dashboardCopy.speciesFallback} - ${
                  recentResult.totalFishCaught || 0
                } ${dashboardCopy.fishCount}`
              : dashboardCopy.resultFallback
          }
          image={blackBass}
          compact={compact}
        />
        <ListPanel
          label={dashboardCopy.latestCatch}
          title={recentCatch?.species || dashboardCopy.emptyGallery}
          body={
            recentCatch
              ? `${recentCatch.quantity || 1} ${dashboardCopy.inWord} ${
                  recentCatch.spotName || dashboardCopy.unnamedSpot
                }`
              : dashboardCopy.catchFallback
          }
          image={galleryIcon}
          compact={compact}
        />
      </View>

      {loading && (
        <View style={styles.loadingLine}>
          <ActivityIndicator color="#11c5b7" />
          <Text style={styles.loadingText}>{copy.loading}</Text>
        </View>
      )}
    </View>
  )
}

function MissionAction({ label, detail, image, onPress }) {
  return (
    <Pressable accessibilityRole="button" onPress={onPress} style={styles.actionButton}>
      <Image source={{ uri: image }} style={styles.actionImage} resizeMode="cover" />
      <View style={styles.actionCopy}>
        <Text style={styles.actionLabel}>{label}</Text>
        <Text style={styles.actionDetail}>{detail}</Text>
      </View>
    </Pressable>
  )
}

function SignalTile({ label, value }) {
  return (
    <View style={styles.signalTile}>
      <Text style={styles.signalValue}>{value}</Text>
      <Text style={styles.signalLabel}>{label}</Text>
    </View>
  )
}

function MetricPanel({ label, title, value, image, compact }) {
  return (
    <View style={[styles.metricPanel, compact && styles.panelFull]}>
      <View style={styles.metricCopy}>
        <Text style={styles.panelLabel}>{label}</Text>
        <Text style={styles.panelTitle}>{title}</Text>
        <Text style={styles.panelText}>{value}</Text>
      </View>
      <Image source={{ uri: image }} style={styles.metricImage} resizeMode="cover" />
    </View>
  )
}

function ListPanel({ label, title, body, image, compact }) {
  return (
    <View style={[styles.listPanel, compact && styles.panelFull]}>
      <Image source={{ uri: image }} style={styles.listImage} resizeMode="cover" />
      <View style={styles.listCopy}>
        <Text style={styles.panelLabel}>{label}</Text>
        <Text style={styles.panelTitle}>{title}</Text>
        <Text style={styles.panelText}>{body}</Text>
      </View>
    </View>
  )
}

function FeaturePreview({ section, compact, copy }) {
  return (
    <View style={[styles.featurePanel, compact && styles.featurePanelCompact]}>
      <Image source={{ uri: section.image }} style={styles.featureImage} resizeMode="cover" />
      <View style={styles.featureCopy}>
        <Text style={styles.panelLabel}>{copy.areaLabel}</Text>
        <Text style={styles.featureTitle}>{section.title}</Text>
        <Text style={styles.featureText}>{section.subtitle}</Text>
      </View>
    </View>
  )
}

function InfoChip({ label }) {
  return (
    <View style={styles.infoChip}>
      <Text style={styles.infoChipText}>{label}</Text>
    </View>
  )
}

function formatSchedule(date, time, copy) {
  if (!date) {
    return copy.undefinedDate
  }

  if (!time) {
    return date
  }

  return `${date} ${copy.at} ${String(time).slice(0, 5)}`
}

const styles = StyleSheet.create({
  safeArea: {
    minHeight: '100vh',
    backgroundColor: '#edf3ef',
  },
  shell: {
    minHeight: '100vh',
    flexDirection: 'row',
    backgroundColor: '#edf3ef',
  },
  shellCompact: {
    flexDirection: 'column',
  },
  sidebar: {
    width: 348,
    padding: 20,
    gap: 18,
    backgroundColor: '#082f3f',
    borderRightWidth: 1,
    borderRightColor: '#174b5a',
  },
  sidebarCompact: {
    width: '100%',
    borderRightWidth: 0,
    borderBottomWidth: 1,
    borderBottomColor: '#174b5a',
  },
  brandBlock: {
    minHeight: 92,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
  },
  brandIconFrame: {
    width: 76,
    height: 76,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 8,
    overflow: 'hidden',
    backgroundColor: 'transparent',
  },
  brandIcon: {
    width: 98,
    height: 98,
    marginLeft: -11,
    marginTop: -11,
    borderRadius: 8,
  },
  brandTextBlock: {
    flex: 1,
  },
  brandName: {
    color: '#ffffff',
    fontSize: 22,
    fontWeight: '900',
  },
  brandSubline: {
    marginTop: 3,
    color: '#9ed9df',
    fontSize: 13,
    fontWeight: '700',
  },
  sidebarBottom: {
    marginTop: 'auto',
    gap: 10,
  },
  sidebarBottomCompact: {
    marginTop: 0,
  },
  languageSwitch: {
    minHeight: 46,
    flexDirection: 'row',
    padding: 4,
    borderRadius: 8,
    backgroundColor: 'rgba(255, 255, 255, 0.08)',
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.12)',
  },
  languageOption: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 6,
  },
  languageOptionSelected: {
    backgroundColor: '#e9fffb',
  },
  languageText: {
    color: '#d9f5f2',
    fontSize: 14,
    fontWeight: '900',
  },
  languageTextSelected: {
    color: '#082f3f',
  },
  menuScrollCompact: {
    marginRight: -20,
  },
  menuList: {
    gap: 12,
  },
  menuListCompact: {
    flexDirection: 'row',
    paddingRight: 20,
  },
  menuItem: {
    minHeight: 84,
    minWidth: 180,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    paddingHorizontal: 13,
    paddingVertical: 10,
    borderRadius: 8,
    backgroundColor: 'rgba(255, 255, 255, 0.08)',
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.1)',
  },
  menuItemSelected: {
    backgroundColor: '#e9fffb',
    borderColor: '#66e5d4',
  },
  menuIconCrop: {
    width: 64,
    height: 64,
    borderRadius: 8,
    overflow: 'hidden',
    backgroundColor: 'transparent',
  },
  menuIcon: {
    width: 64,
    height: 64,
    borderRadius: 8,
  },
  menuText: {
    color: '#d9f5f2',
    fontSize: 17,
    fontWeight: '800',
  },
  menuTextSelected: {
    color: '#082f3f',
  },
  sidebarStatus: {
    minHeight: 52,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingHorizontal: 12,
    borderRadius: 8,
    backgroundColor: 'rgba(255, 255, 255, 0.08)',
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.12)',
  },
  sidebarStatusText: {
    color: '#d9f5f2',
    fontSize: 14,
    fontWeight: '800',
  },
  content: {
    flex: 1,
  },
  contentInner: {
    padding: 26,
    gap: 16,
  },
  topBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    gap: 18,
  },
  topBarCompact: {
    flexDirection: 'column',
  },
  kicker: {
    color: '#0f766e',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  screenTitle: {
    marginTop: 4,
    color: '#102421',
    fontSize: 36,
    lineHeight: 42,
    fontWeight: '900',
  },
  screenIntro: {
    maxWidth: 720,
    marginTop: 7,
    color: '#53645d',
    fontSize: 16,
    lineHeight: 23,
    fontWeight: '600',
  },
  backendPill: {
    minWidth: 154,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    paddingHorizontal: 12,
    paddingVertical: 9,
    borderRadius: 999,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  statusDot: {
    width: 9,
    height: 9,
    borderRadius: 9,
  },
  statusDotOk: {
    backgroundColor: '#16a34a',
  },
  statusDotOff: {
    backgroundColor: '#dc2626',
  },
  backendText: {
    color: '#34413b',
    fontSize: 13,
    fontWeight: '800',
  },
  notice: {
    padding: 13,
    borderRadius: 8,
    backgroundColor: '#fff8ec',
    borderWidth: 1,
    borderColor: '#efc98b',
  },
  noticeText: {
    color: '#8b4b19',
    fontSize: 14,
    fontWeight: '800',
  },
  dashboardStack: {
    gap: 14,
  },
  focusPanel: {
    minHeight: 304,
    overflow: 'hidden',
    borderRadius: 8,
    backgroundColor: '#082f3f',
  },
  focusImage: {
    opacity: 0.82,
  },
  focusOverlay: {
    flex: 1,
    justifyContent: 'space-between',
    padding: 24,
    backgroundColor: 'rgba(4, 26, 34, 0.5)',
  },
  focusCopy: {
    maxWidth: 760,
  },
  focusLabel: {
    color: '#f2d18d',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  focusTitle: {
    maxWidth: 760,
    marginTop: 8,
    color: '#ffffff',
    fontSize: 36,
    lineHeight: 42,
    fontWeight: '900',
  },
  focusText: {
    maxWidth: 680,
    marginTop: 8,
    color: '#edf7f0',
    fontSize: 16,
    lineHeight: 23,
    fontWeight: '700',
  },
  focusChips: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginTop: 22,
  },
  infoChip: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 999,
    backgroundColor: 'rgba(255, 255, 255, 0.18)',
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.26)',
  },
  infoChipText: {
    color: '#ffffff',
    fontSize: 13,
    fontWeight: '900',
  },
  actionGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  actionButton: {
    flexGrow: 1,
    flexBasis: 240,
    minHeight: 82,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    padding: 12,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  actionImage: {
    width: 54,
    height: 54,
    borderRadius: 8,
    backgroundColor: '#e6ece4',
  },
  actionCopy: {
    flex: 1,
  },
  actionLabel: {
    color: '#102421',
    fontSize: 17,
    fontWeight: '900',
  },
  actionDetail: {
    marginTop: 3,
    color: '#5a6b63',
    fontSize: 13,
    lineHeight: 18,
    fontWeight: '700',
  },
  signalGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  signalTile: {
    flexGrow: 1,
    flexBasis: 130,
    minHeight: 88,
    justifyContent: 'center',
    padding: 14,
    borderRadius: 8,
    backgroundColor: '#dff8f3',
    borderWidth: 1,
    borderColor: '#b8e9df',
  },
  signalValue: {
    color: '#082f3f',
    fontSize: 28,
    lineHeight: 32,
    fontWeight: '900',
  },
  signalLabel: {
    marginTop: 4,
    color: '#27615d',
    fontSize: 13,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  panelGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 14,
  },
  metricPanel: {
    flexGrow: 1,
    flexBasis: 320,
    minHeight: 160,
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 14,
    padding: 16,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  listPanel: {
    flexGrow: 1,
    flexBasis: 320,
    minHeight: 154,
    flexDirection: 'row',
    gap: 14,
    padding: 16,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  panelFull: {
    flexBasis: '100%',
  },
  metricCopy: {
    flex: 1,
  },
  listCopy: {
    flex: 1,
    justifyContent: 'center',
  },
  panelLabel: {
    color: '#6d7b75',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  panelTitle: {
    marginTop: 8,
    color: '#102421',
    fontSize: 21,
    lineHeight: 26,
    fontWeight: '900',
  },
  panelText: {
    marginTop: 7,
    color: '#5a6b63',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '600',
  },
  metricImage: {
    width: 92,
    height: 92,
    borderRadius: 8,
    backgroundColor: '#e6ece4',
  },
  listImage: {
    width: 96,
    minHeight: 96,
    borderRadius: 8,
    backgroundColor: '#e6ece4',
  },
  featurePanel: {
    maxWidth: 900,
    minHeight: 280,
    flexDirection: 'row',
    gap: 20,
    padding: 18,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  featurePanelCompact: {
    flexDirection: 'column',
  },
  featureImage: {
    width: 220,
    minHeight: 220,
    borderRadius: 8,
    backgroundColor: '#e6ece4',
  },
  featureCopy: {
    flex: 1,
    justifyContent: 'center',
  },
  featureTitle: {
    marginTop: 8,
    color: '#102421',
    fontSize: 28,
    lineHeight: 34,
    fontWeight: '900',
  },
  featureText: {
    maxWidth: 520,
    marginTop: 8,
    color: '#5a6b63',
    fontSize: 16,
    lineHeight: 23,
    fontWeight: '600',
  },
  loadingLine: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    minHeight: 32,
  },
  loadingText: {
    color: '#5a6b63',
    fontSize: 14,
    fontWeight: '800',
  },
})

export default App
