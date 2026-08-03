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
  TextInput,
  View,
  useWindowDimensions,
} from 'react-native'

import appIcon from '../assets/images/brand/app-icon.png'
import barbel from '../assets/images/fish/freshwater/barbel.png'
import blackBass from '../assets/images/fish/freshwater/black-bass.png'
import pike from '../assets/images/fish/freshwater/pike.png'
import dashboardIcon from '../assets/images/ui/dashboard-icon.png'
import fishingPlanIcon from '../assets/images/ui/fishingplan-icon.png'
import galleryIcon from '../assets/images/ui/gallery-icon.png'
import libraryIcon from '../assets/images/ui/library-icon.png'
import lureBoxIcon from '../assets/images/ui/lurebox-icon.png'
import profileIcon from '../assets/images/ui/profile-icon.png'
import sessionIcon from '../assets/images/ui/session-icon.png'
import damSpot from '../assets/images/spots/dam.png'
import harborSpot from '../assets/images/spots/harbor-platform.png'
import lakeSpot from '../assets/images/spots/lake.png'
import riverSpot from '../assets/images/spots/river.png'
import spotsIcon from '../assets/images/ui/spots-icon.png'
import clearSky from '../assets/images/weather/clear-sky.png'
import cloudySky from '../assets/images/weather/cloudy.png'
import crankbait from '../assets/images/lures/crankbait.png'
import jerkbait from '../assets/images/lures/jerkbait.png'
import popper from '../assets/images/lures/popper.png'
import senko from '../assets/images/lures/senko.png'
import spinnerbait from '../assets/images/lures/spinnerbait.png'
import bottomFishingAction from '../assets/images/lure-actions/bottom-fishing.png'
import bottomFishingIcon from '../assets/images/lure-action-icons/bottom-fishing-icon.png'
import crankRetrieveAction from '../assets/images/lure-actions/crank-retrieve.png'
import crankRetrieveIcon from '../assets/images/lure-action-icons/crank-retrieve-icon.png'
import jerkTwitchAction from '../assets/images/lure-actions/jerk-twitch.png'
import jerkTwitchIcon from '../assets/images/lure-action-icons/jerk-twitch-icon.png'
import jiggingAction from '../assets/images/lure-actions/jigging.png'
import jiggingIcon from '../assets/images/lure-action-icons/jigging-icon.png'
import topWaterAction from '../assets/images/lure-actions/top-water.png'
import topWaterIcon from '../assets/images/lure-action-icons/top-water-icon.png'

const lureActionCatalog = [
  { id: 'bottom-fishing', icon: bottomFishingIcon, image: bottomFishingAction, terms: ['bottom', 'fundo', 'drag', 'hops', 'soft bait'] },
  { id: 'jigging', icon: jiggingIcon, image: jiggingAction, terms: ['jig', 'jigging', 'hops', 'vertical'] },
  { id: 'crank-retrieve', icon: crankRetrieveIcon, image: crankRetrieveAction, terms: ['crank', 'retrieve', 'spinner', 'spoon', 'roll'] },
  { id: 'jerk-twitch', icon: jerkTwitchIcon, image: jerkTwitchAction, terms: ['jerk', 'twitch', 'jerkbait', 'stop-and-go'] },
  { id: 'top-water', icon: topWaterIcon, image: topWaterAction, terms: ['topwater', 'top water', 'surface', 'popper', 'frog'] },
]

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
  gallery: blackBass,
  spots: lakeSpot,
  plans: harborSpot,
  session: riverSpot,
  lureBox: spinnerbait,
  library: pike,
  profile: profileIcon,
}

const sectionThemes = {
  gallery: { accent: '#e66f51', surface: '#fff8f1', visual: '#f7dfd0', border: '#efd0bd' },
  spots: { accent: '#1687a7', surface: '#eef9fb', visual: '#d9eef1', border: '#c2e1e7' },
  plans: { accent: '#c58a2b', surface: '#fff9ea', visual: '#f4e5bd', border: '#ecd59d' },
  session: { accent: '#8862bd', surface: '#f7f2fd', visual: '#e7dcf5', border: '#d9c8eb' },
  lureBox: { accent: '#dc5f8a', surface: '#fff2f6', visual: '#f5d8e4', border: '#ebc6d5' },
  library: { accent: '#2b8c68', surface: '#effaf4', visual: '#d9eee1', border: '#c2e2cf' },
  profile: { accent: '#2c76c7', surface: '#f0f6ff', visual: '#d9e9fb', border: '#c5dcef' },
}

const groupTones = {
  catches: { backgroundColor: '#fffaf5', borderColor: '#efd9c5', accent: '#e66f51', imageBackground: '#f7e5d9' },
  spots: { backgroundColor: '#f4fbfc', borderColor: '#c8e5ea', accent: '#1687a7', imageBackground: '#dceff2' },
  plans: { backgroundColor: '#fffdf4', borderColor: '#eddda9', accent: '#c58a2b', imageBackground: '#f5e9c7' },
  sessions: { backgroundColor: '#faf7ff', borderColor: '#ded1ef', accent: '#8862bd', imageBackground: '#e9def5' },
  lureBox: { backgroundColor: '#fff7fa', borderColor: '#efd0dc', accent: '#dc5f8a', imageBackground: '#f6dce7' },
  fish: { backgroundColor: '#f3fbf6', borderColor: '#c9e5d2', accent: '#2b8c68', imageBackground: '#dcefe3' },
  lureLibrary: { backgroundColor: '#f4f8ff', borderColor: '#cdddf1', accent: '#2c76c7', imageBackground: '#dfeafa' },
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
    resources: {
      loadError: 'Nao foi possivel carregar os dados deste ecra.',
      empty: 'Sem dados para mostrar.',
      total: 'total',
      searchPlaceholder: 'Pesquisar por nome, especie, spot ou lure',
      clear: 'Limpar',
      previous: 'Anterior',
      next: 'Seguinte',
      page: 'Pagina',
      details: 'Detalhes',
      close: 'Fechar',
      viewDetails: 'Ver detalhes',
      loadingDetails: 'A carregar detalhe...',
      detailLoadError: 'Nao foi possivel carregar o detalhe completo.',
      createSpot: 'Novo spot',
      saveSpot: 'Guardar spot',
      saving: 'A guardar...',
      cancel: 'Cancelar',
      createSpotSuccess: 'Spot criado com sucesso.',
      createSpotError: 'Nao foi possivel criar o spot.',
      requiredFields: 'Preenche nome, latitude, longitude e tipo de agua.',
      createPlan: 'Novo plano',
      savePlan: 'Guardar plano',
      createPlanSuccess: 'Plano criado com sucesso.',
      createPlanError: 'Nao foi possivel criar o plano.',
      planRequiredFields: 'Escolhe spot, data, especie alvo, claridade e nivel da agua.',
      chooseSpot: 'Escolher spot',
      noSpotsForPlan: 'Cria primeiro um spot para poderes planear uma sessao.',
      plannedDateHint: 'YYYY-MM-DD',
      plannedTimeHint: 'HH:MM',
      waterClarityOptions: {
        clear: 'Clara',
        stained: 'Manchada',
        muddy: 'Turva',
      },
      waterLevelOptions: {
        low: 'Baixo',
        normal: 'Normal',
        high: 'Alto',
      },
      workspace: {
        fieldAtlas: 'Atlas de terreno',
        fieldAtlasText: 'Pontos de agua, coordenadas e memoria de campo.',
        missionBoard: 'Quadro de missao',
        missionBoardText: 'Decisoes preparadas para a proxima saida.',
        liveConsole: 'Consola da sessao',
        liveConsoleText: 'O que esta a acontecer agora junto a agua.',
        gearInventory: 'Inventario de equipamento',
        gearInventoryText: 'O que tens contigo antes de sair.',
        nextMove: 'Proximo movimento',
        planned: 'planeados',
        stored: 'guardadas',
        ready: 'pronto',
      },
      galleryImages: 'Imagens',
      galleryCountLabel: 'capturas guardadas',
      galleryMemory: 'Memorias de campo',
      galleryMemoryHint: 'Cada imagem guarda uma historia, uma decisao e um lugar.',
      newCatch: 'Nova captura',
      editCatch: 'Editar captura',
      saveCatch: 'Guardar captura',
      catchSaved: 'Captura guardada com sucesso.',
      catchSaveError: 'Nao foi possivel guardar a captura.',
      catchDeleted: 'Captura apagada com sucesso.',
      catchDeleteError: 'Nao foi possivel apagar a captura.',
      catchDeleteConfirm: 'Queres mesmo apagar esta captura?',
      chooseSpecies: 'Escolher especie da biblioteca',
      chooseLure: 'Escolher lure da biblioteca',
      chooseSession: 'Escolher sessao',
      noSessionsAvailable: 'Cria primeiro uma sessao para registar a captura.',
      noFishAvailable: 'Adiciona primeiro especies na biblioteca.',
      noLureSelected: 'Sem lure registada',
      chooseCatchPhoto: 'Adicionar foto do peixe',
      catchPhotoHint: 'Usa uma imagem do computador para dar vida a esta captura.',
      catchImageSelected: 'Foto selecionada',
      catchEditorHint: 'Liga a captura a uma especie, lure e sessao que ja existem na app.',
      lureUsed: 'Lure usada',
      captureSession: 'Sessao da captura',
      libraryFishHint: 'Especies, habitats e padroes de atividade.',
      libraryLureHint: 'Tecnicas, dificuldade e eficacia em campo.',
      actionGuideTitle: 'Guia de acao',
      actionGuideHint: 'Seleciona um movimento para ver a referencia visual.',
      actionGuideOpen: 'Clica no icone para abrir a referencia',
      actionGuideBack: 'Clica na imagem para voltar ao icone',
      actionGuideVisual: 'Referencia visual',
      actionOptions: 'acoes',
      newFish: 'Nova especie',
      newLure: 'Nova lure',
      editEntry: 'Editar',
      deleteEntry: 'Apagar',
      saveEntry: 'Guardar ficha',
      chooseImage: 'Escolher imagem',
      chooseActionIcon: 'Escolher icone da acao',
      chooseActionImage: 'Escolher imagem grande da acao',
      customImageHint: 'Podes escolher uma imagem do computador ou usar um asset existente.',
      manualActionHint: 'Escolhe manualmente o unico icone e a imagem grande associados a esta lure.',
      requiredLibraryFields: 'Preenche pelo menos o nome e o tipo.',
      saveEntrySuccess: 'Ficha guardada com sucesso.',
      saveEntryError: 'Nao foi possivel guardar a ficha.',
      deleteEntrySuccess: 'Ficha apagada com sucesso.',
      deleteEntryError: 'Nao foi possivel apagar a ficha.',
      deleteConfirm: 'Queres mesmo apagar esta ficha?',
      actionImageLabel: 'Imagem grande da acao',
      actionIconLabel: 'Icone da acao',
      imageSelected: 'Imagem selecionada',
      chooseLevel: 'Escolher nivel',
      waterEnvironment: 'Ambiente da agua',
      allEnvironments: 'Todos',
      freshwater: 'Agua doce',
      saltwater: 'Agua salgada',
      chooseWaterEnvironment: 'Escolhe o ambiente',
      requiredWaterEnvironment: 'Escolhe agua doce ou agua salgada.',
      unclassified: 'Sem classificacao',
      selectMultiple: 'Podes escolher varias opcoes',
      chooseOptions: 'Selecionar opcoes',
      noLuresAvailable: 'Adiciona primeiro lures na biblioteca.',
      noOptionsAvailable: 'Sem opcoes disponiveis.',
      removeSelection: 'Remover selecao',
      fishZoneOptions: {
        surface: 'Superficie',
        midWater: 'Meia agua',
        bottom: 'Fundo',
        vegetation: 'Vegetacao',
        structure: 'Estruturas',
        bank: 'Margens',
      },
      commonZoneOptions: {
        shallows: 'Margens rasas',
        deepWater: 'Agua funda',
        dropOffs: 'Quebras de profundidade',
        vegetation: 'Vegetacao',
        rockyAreas: 'Zonas rochosas',
        structures: 'Estruturas submersas',
        current: 'Correntes e entradas',
        openWater: 'Agua aberta',
      },
      levelOptions: {
        easy: 'Facil',
        medium: 'Media',
        hard: 'Dificil',
        low: 'Baixa',
        high: 'Alta',
      },
      actionTypes: {
        'bottom-fishing': { label: 'Pesca no fundo', description: 'Arrasta devagar, cria pequenos saltos e deixa a lure trabalhar junto ao fundo.' },
        jigging: { label: 'Jigging', description: 'Levanta e deixa cair a lure em ciclos curtos, mantendo contacto com a zona de pesca.' },
        'crank-retrieve': { label: 'Recuperacao continua', description: 'Recupera de forma constante e varia a velocidade para cobrir agua.' },
        'jerk-twitch': { label: 'Jerk e twitch', description: 'Aplica toques secos, pequenas pausas e mudancas de ritmo para dar vida a lure.' },
        'top-water': { label: 'Acao de superficie', description: 'Trabalha a lure no topo com toques curtos e pausas para provocar ataques visuais.' },
      },
      openSession: 'Abrir sessao',
      status: 'Estado',
      active: 'Ativo',
      inactive: 'Inativo',
      yes: 'sim',
      no: 'nao',
      profileReady: 'Perfil preparado',
      profileBody: 'Area reservada para preferencias, idioma, especies favoritas e definicoes quando adicionarmos autenticacao.',
      noAuth: 'Sem autenticacao no MVP',
      noAuthBody: 'Por agora a app trabalha em modo local e usa dados do backend sem login.',
      groups: {
        catches: 'Capturas recentes',
        spots: 'Spots guardados',
        plans: 'Planos recentes',
        sessions: 'Sessoes recentes',
        lureBox: 'Lure Box pessoal',
        fish: 'Biblioteca de peixes',
        lureLibrary: 'Biblioteca de lures',
      },
      fields: {
        name: 'Nome',
        spot: 'Spot',
        species: 'Especie',
        waterType: 'Tipo de agua',
        favoriteSpecies: 'Especies favoritas',
        coordinates: 'Coordenadas',
        latitude: 'Latitude',
        longitude: 'Longitude',
        description: 'Descricao',
        plannedFor: 'Planeado para',
        targetSpecies: 'Especie alvo',
        waterClarity: 'Claridade',
        waterLevel: 'Nivel da agua',
        notes: 'Notas',
        status: 'Estado',
        result: 'Resultado',
        finalNotes: 'Notas finais',
        rating: 'Avaliacao',
        duration: 'Duracao',
        type: 'Tipo',
        color: 'Cor',
        size: 'Tamanho',
        weight: 'Peso',
        brand: 'Marca',
        quantity: 'Quantidade',
        condition: 'Condicao',
        active: 'Ativa',
        personalNotes: 'Notas pessoais',
        habitat: 'Habitat',
        activeTimes: 'Horas ativas',
        strikeZone: 'Zona de ataque',
        zones: 'Zonas comuns',
        favoriteLures: 'Lures favoritas',
        difficulty: 'Dificuldade',
        effectiveness: 'Eficacia',
        action: 'Acao',
        idealConditions: 'Condicoes ideais',
        released: 'Libertado',
        session: 'Sessao',
        photo: 'Foto',
        createdAt: 'Criado em',
      },
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
    resources: {
      loadError: 'Could not load data for this screen.',
      empty: 'No data to show.',
      total: 'total',
      searchPlaceholder: 'Search by name, species, spot or lure',
      clear: 'Clear',
      previous: 'Previous',
      next: 'Next',
      page: 'Page',
      details: 'Details',
      close: 'Close',
      viewDetails: 'View details',
      loadingDetails: 'Loading details...',
      detailLoadError: 'Could not load the full detail.',
      createSpot: 'New spot',
      saveSpot: 'Save spot',
      saving: 'Saving...',
      cancel: 'Cancel',
      createSpotSuccess: 'Spot created successfully.',
      createSpotError: 'Could not create the spot.',
      requiredFields: 'Fill name, latitude, longitude and water type.',
      createPlan: 'New plan',
      savePlan: 'Save plan',
      createPlanSuccess: 'Plan created successfully.',
      createPlanError: 'Could not create the plan.',
      planRequiredFields: 'Choose spot, date, target species, water clarity and water level.',
      chooseSpot: 'Choose spot',
      noSpotsForPlan: 'Create a spot first so you can plan a session.',
      plannedDateHint: 'YYYY-MM-DD',
      plannedTimeHint: 'HH:MM',
      waterClarityOptions: {
        clear: 'Clear',
        stained: 'Stained',
        muddy: 'Muddy',
      },
      waterLevelOptions: {
        low: 'Low',
        normal: 'Normal',
        high: 'High',
      },
      workspace: {
        fieldAtlas: 'Field atlas',
        fieldAtlasText: 'Water points, coordinates and field memory.',
        missionBoard: 'Mission board',
        missionBoardText: 'Decisions prepared for the next outing.',
        liveConsole: 'Session console',
        liveConsoleText: 'What is happening right now by the water.',
        gearInventory: 'Gear inventory',
        gearInventoryText: 'What you have with you before heading out.',
        nextMove: 'Next move',
        planned: 'planned',
        stored: 'stored',
        ready: 'ready',
      },
      galleryImages: 'Images',
      galleryCountLabel: 'saved catches',
      galleryMemory: 'Field memories',
      galleryMemoryHint: 'Every image keeps a story, a decision and a place.',
      newCatch: 'New catch',
      editCatch: 'Edit catch',
      saveCatch: 'Save catch',
      catchSaved: 'Catch saved successfully.',
      catchSaveError: 'The catch could not be saved.',
      catchDeleted: 'Catch deleted successfully.',
      catchDeleteError: 'The catch could not be deleted.',
      catchDeleteConfirm: 'Do you really want to delete this catch?',
      chooseSpecies: 'Choose species from library',
      chooseLure: 'Choose lure from library',
      chooseSession: 'Choose session',
      noSessionsAvailable: 'Create a session first to register the catch.',
      noFishAvailable: 'Add species to the library first.',
      noLureSelected: 'No lure recorded',
      chooseCatchPhoto: 'Add fish photo',
      catchPhotoHint: 'Use an image from your computer to bring this catch to life.',
      catchImageSelected: 'Photo selected',
      catchEditorHint: 'Connect the catch to a species, lure and session already in the app.',
      lureUsed: 'Lure used',
      captureSession: 'Catch session',
      libraryFishHint: 'Species, habitats and activity patterns.',
      libraryLureHint: 'Techniques, difficulty and effectiveness in the field.',
      actionGuideTitle: 'Action guide',
      actionGuideHint: 'Choose a movement to see its visual reference.',
      actionGuideOpen: 'Click the icon to open the reference',
      actionGuideBack: 'Click the image to return to the icon',
      actionGuideVisual: 'Visual reference',
      actionOptions: 'actions',
      newFish: 'New species',
      newLure: 'New lure',
      editEntry: 'Edit',
      deleteEntry: 'Delete',
      saveEntry: 'Save entry',
      chooseImage: 'Choose image',
      chooseActionIcon: 'Choose action icon',
      chooseActionImage: 'Choose large action image',
      customImageHint: 'Choose an image from your computer or use an existing asset.',
      manualActionHint: 'Manually choose the single icon and large image linked to this lure.',
      requiredLibraryFields: 'Fill in at least the name and type.',
      saveEntrySuccess: 'Entry saved successfully.',
      saveEntryError: 'The entry could not be saved.',
      deleteEntrySuccess: 'Entry deleted successfully.',
      deleteEntryError: 'The entry could not be deleted.',
      deleteConfirm: 'Do you really want to delete this entry?',
      actionImageLabel: 'Large action image',
      actionIconLabel: 'Action icon',
      imageSelected: 'Image selected',
      chooseLevel: 'Choose level',
      waterEnvironment: 'Water environment',
      allEnvironments: 'All',
      freshwater: 'Freshwater',
      saltwater: 'Saltwater',
      chooseWaterEnvironment: 'Choose the environment',
      requiredWaterEnvironment: 'Choose freshwater or saltwater.',
      unclassified: 'Unclassified',
      selectMultiple: 'You can choose more than one option',
      chooseOptions: 'Select options',
      noLuresAvailable: 'Add lures to the library first.',
      noOptionsAvailable: 'No options available.',
      removeSelection: 'Remove selection',
      fishZoneOptions: {
        surface: 'Surface',
        midWater: 'Mid-water',
        bottom: 'Bottom',
        vegetation: 'Vegetation',
        structure: 'Structures',
        bank: 'Banks',
      },
      commonZoneOptions: {
        shallows: 'Shallows',
        deepWater: 'Deep water',
        dropOffs: 'Drop-offs',
        vegetation: 'Vegetation',
        rockyAreas: 'Rocky areas',
        structures: 'Submerged structures',
        current: 'Current and inlets',
        openWater: 'Open water',
      },
      levelOptions: {
        easy: 'Easy',
        medium: 'Medium',
        hard: 'Hard',
        low: 'Low',
        high: 'High',
      },
      actionTypes: {
        'bottom-fishing': { label: 'Bottom fishing', description: 'Drag slowly, add short hops and keep the lure working close to the bottom.' },
        jigging: { label: 'Jigging', description: 'Lift and drop the lure in short cycles while keeping contact with the fishing zone.' },
        'crank-retrieve': { label: 'Steady retrieve', description: 'Retrieve steadily and vary the speed to cover water efficiently.' },
        'jerk-twitch': { label: 'Jerk and twitch', description: 'Use sharp twitches, short pauses and rhythm changes to give the lure life.' },
        'top-water': { label: 'Topwater action', description: 'Work the lure on the surface with short taps and pauses to trigger visual strikes.' },
      },
      openSession: 'Open session',
      status: 'Status',
      active: 'Active',
      inactive: 'Inactive',
      yes: 'yes',
      no: 'no',
      profileReady: 'Profile prepared',
      profileBody: 'Reserved area for preferences, language, favorite species and settings when authentication is added.',
      noAuth: 'No authentication in the MVP',
      noAuthBody: 'For now the app works locally and uses backend data without login.',
      groups: {
        catches: 'Recent catches',
        spots: 'Saved spots',
        plans: 'Recent plans',
        sessions: 'Recent sessions',
        lureBox: 'Personal Lure Box',
        fish: 'Fish library',
        lureLibrary: 'Lure library',
      },
      fields: {
        name: 'Name',
        spot: 'Spot',
        species: 'Species',
        waterType: 'Water type',
        favoriteSpecies: 'Favorite species',
        coordinates: 'Coordinates',
        latitude: 'Latitude',
        longitude: 'Longitude',
        description: 'Description',
        plannedFor: 'Planned for',
        targetSpecies: 'Target species',
        waterClarity: 'Water clarity',
        waterLevel: 'Water level',
        notes: 'Notes',
        status: 'Status',
        result: 'Result',
        finalNotes: 'Final notes',
        rating: 'Rating',
        duration: 'Duration',
        type: 'Type',
        color: 'Color',
        size: 'Size',
        weight: 'Weight',
        brand: 'Brand',
        quantity: 'Quantity',
        condition: 'Condition',
        active: 'Active',
        personalNotes: 'Personal notes',
        habitat: 'Habitat',
        activeTimes: 'Active times',
        strikeZone: 'Strike zone',
        zones: 'Common zones',
        favoriteLures: 'Favorite lures',
        difficulty: 'Difficulty',
        effectiveness: 'Effectiveness',
        action: 'Action',
        idealConditions: 'Ideal conditions',
        released: 'Released',
        session: 'Session',
        photo: 'Photo',
        createdAt: 'Created at',
      },
    },
  },
}

const sectionQueryConfig = {
  gallery: [
    {
      key: 'catches',
      path: '/api/gallery/catches',
      params: { withPhotoOnly: 'false', sortDirection: 'desc' },
      size: 8,
      image: galleryIcon,
    },
  ],
  spots: [
    {
      key: 'spots',
      path: '/api/spots',
      params: { sortDirection: 'desc' },
      size: 8,
      image: spotsIcon,
    },
  ],
  plans: [
    {
      key: 'plans',
      path: '/api/plans',
      size: 8,
      image: fishingPlanIcon,
    },
  ],
  session: [
    {
      key: 'sessions',
      path: '/api/sessions',
      size: 8,
      image: sessionIcon,
    },
  ],
  lureBox: [
    {
      key: 'lureBox',
      path: '/api/lure-box',
      size: 8,
      image: lureBoxIcon,
    },
  ],
  library: [
    {
      key: 'fish',
      path: '/api/fish',
      size: 9,
      image: blackBass,
    },
    {
      key: 'lureLibrary',
      path: '/api/lure-library',
      size: 9,
      image: libraryIcon,
    },
  ],
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

const emptyDetailState = {
  sectionId: null,
  group: null,
  item: null,
  data: null,
  loading: false,
  error: false,
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
  const [sectionState, setSectionState] = useState({
    sectionId: null,
    groups: [],
    loading: false,
    error: false,
  })
  const [sectionControls, setSectionControls] = useState({})
  const [detailState, setDetailState] = useState(emptyDetailState)
  const [sectionRefreshKey, setSectionRefreshKey] = useState(0)
  const copy = translations[language]
  const sectionSearch = sectionControls[activeSection]?.search ?? ''
  const sectionPage = sectionControls[activeSection]?.page ?? 0
  const sectionWaterEnvironment = sectionControls[activeSection]?.waterEnvironment ?? 'ALL'

  function navigateToSection(sectionId) {
    setDetailState(emptyDetailState)
    setActiveSection(sectionId)
  }

  function updateSectionControls(updates) {
    setDetailState(emptyDetailState)
    setSectionControls((current) => ({
      ...current,
      [activeSection]: {
        search: current[activeSection]?.search ?? '',
        page: current[activeSection]?.page ?? 0,
        ...updates,
      },
    }))
  }

  function refreshActiveSection() {
    setDetailState(emptyDetailState)
    setSectionRefreshKey((current) => current + 1)
  }

  async function openResourceDetail(item, group) {
    const detailUrl = buildDetailUrl(group, item)

    setDetailState({
      sectionId: activeSection,
      group,
      item,
      data: null,
      loading: Boolean(detailUrl),
      error: false,
    })

    if (!detailUrl) {
      return
    }

    try {
      const response = await fetch(detailUrl)

      if (!response.ok) {
        throw new Error('Detail unavailable')
      }

      const data = await response.json()

      setDetailState((current) => {
        if (current.sectionId !== activeSection || current.item !== item) {
          return current
        }

        return {
          ...current,
          data,
          loading: false,
          error: false,
        }
      })
    } catch {
      setDetailState((current) => {
        if (current.sectionId !== activeSection || current.item !== item) {
          return current
        }

        return {
          ...current,
          loading: false,
          error: true,
        }
      })
    }
  }

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

  useEffect(() => {
    let ignore = false
    const queries = sectionQueryConfig[activeSection]

    if (!queries) {
      return undefined
    }

    async function loadSection() {
      setSectionState({
        sectionId: activeSection,
        groups: [],
        loading: true,
        error: false,
      })

      try {
        const groups = await Promise.all(
          queries.map(async (query) => {
            const response = await fetch(buildSectionUrl(query, sectionSearch, sectionPage, { waterEnvironment: sectionWaterEnvironment }))

            if (!response.ok) {
              throw new Error('Section unavailable')
            }

            const payload = await response.json()

            return {
              ...query,
              items: payload.items || [],
              totalItems: payload.totalItems ?? payload.items?.length ?? 0,
              page: payload.page ?? sectionPage,
              totalPages: payload.totalPages ?? 1,
              hasNext: payload.hasNext ?? false,
              hasPrevious: payload.hasPrevious ?? false,
            }
          }),
        )

        if (!ignore) {
          setSectionState({
            sectionId: activeSection,
            groups,
            loading: false,
            error: false,
          })
        }
      } catch {
        if (!ignore) {
          setSectionState({
            sectionId: activeSection,
            groups: [],
            loading: false,
            error: true,
          })
        }
      }
    }

    loadSection()

    return () => {
      ignore = true
    }
  }, [activeSection, sectionPage, sectionRefreshKey, sectionSearch, sectionWaterEnvironment])

  const activeCopy = useMemo(
    () => ({
      id: activeSection,
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
                  onPress={() => navigateToSection(item.id)}
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
          <View
            style={[
              styles.topBar,
              (activeSection === 'gallery' || activeSection === 'library') && styles.galleryTopBar,
              compact && styles.topBarCompact,
            ]}
          >
            {activeSection === 'gallery' || activeSection === 'library' ? null : (
              <View>
                <Text style={styles.kicker}>{copy.today}</Text>
                <Text style={styles.screenTitle}>{activeCopy.title}</Text>
                <Text style={styles.screenIntro}>{activeCopy.subtitle}</Text>
              </View>
            )}

            <View style={styles.topBarSide}>
              <View style={styles.workspacePill}>
                <View style={styles.workspacePillDot} />
                <Text style={styles.workspacePillText}>{copy.menu[activeSection]}</Text>
              </View>
              <View style={styles.backendPill}>
                <View style={[styles.statusDot, health ? styles.statusDotOk : styles.statusDotOff]} />
                <Text style={styles.backendText}>{health ? copy.backendOnline : copy.backendOffline}</Text>
              </View>
            </View>
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
              onNavigate={navigateToSection}
              copy={copy}
            />
          ) : activeSection === 'profile' ? (
            <ProfileScreen compact={compact} copy={copy} />
          ) : (
            <ResourceScreen
              section={activeCopy}
              groups={sectionState.sectionId === activeSection ? sectionState.groups : []}
              loading={sectionState.loading}
              error={sectionState.error}
              detail={detailState.sectionId === activeSection ? detailState : emptyDetailState}
              onOpenDetail={openResourceDetail}
              onCloseDetail={() => setDetailState(emptyDetailState)}
              onCreated={refreshActiveSection}
              search={sectionSearch}
              onSearchChange={(value) => updateSectionControls({ search: value, page: 0 })}
              onPageChange={(page) => updateSectionControls({ page })}
              compact={compact}
              waterEnvironment={sectionWaterEnvironment}
              onWaterEnvironmentChange={(value) => updateSectionControls({ waterEnvironment: value, page: 0 })}
              copy={copy}
            />
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
    <View style={styles.commandDashboard}>
      <ImageBackground
        source={{ uri: damSpot }}
        style={styles.commandHero}
        imageStyle={styles.commandHeroImage}
        resizeMode="cover"
      >
        <View style={styles.commandHeroOverlay}>
          <View style={styles.commandHeroMain}>
            <Text style={styles.commandHeroKicker}>{dashboardCopy.focusLabel}</Text>
            <Text style={styles.commandHeroTitle}>{nextSession?.spotName || dashboardCopy.focusFallbackTitle}</Text>
            <Text style={styles.commandHeroText}>
              {nextSession
                ? `${nextSession.targetSpecies || dashboardCopy.speciesFallback} - ${formatSchedule(
                    nextSession.date,
                    nextSession.time,
                    copy,
                  )}`
                : dashboardCopy.focusFallbackText}
            </Text>
          </View>

          <View style={styles.commandHeroPulses}>
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
        <SignalTile label={dashboardCopy.spots} value={dashboard.totalSpots || 0} tone="blue" />
        <SignalTile label={dashboardCopy.plans} value={dashboard.totalPlans || 0} tone="gold" />
        <SignalTile label={dashboardCopy.sessions} value={dashboard.totalSessions || 0} tone="violet" />
        <SignalTile
          label={dashboardCopy.lures}
          value={dashboard.totalLures || dashboard.totalLureLibraryItems || 0}
          tone="pink"
        />
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
          image={bestLure ? spinnerbait : popper}
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
          image={weather ? clearSky : cloudySky}
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
          image={barbel}
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
    <Pressable accessibilityRole="button" onPress={onPress} style={styles.commandActionButton}>
      <Image source={{ uri: image }} style={styles.commandActionImage} resizeMode="cover" />
      <View style={styles.commandActionCopy}>
        <Text style={styles.commandActionLabel}>{label}</Text>
        <Text style={styles.commandActionDetail}>{detail}</Text>
      </View>
      <Text style={styles.commandActionArrow}>+</Text>
    </Pressable>
  )
}

function SignalTile({ label, value, tone }) {
  return (
    <View style={[styles.commandStat, tone === 'gold' && styles.commandStatGold, tone === 'violet' && styles.commandStatViolet, tone === 'pink' && styles.commandStatPink]}>
      <Text style={styles.commandStatValue}>{value}</Text>
      <Text style={styles.commandStatLabel}>{label}</Text>
    </View>
  )
}

function MetricPanel({ label, title, value, image, compact }) {
  return (
    <View style={[styles.commandMetricPanel, compact && styles.panelFull]}>
      <View style={styles.commandMetricCopy}>
        <Text style={styles.commandPanelLabel}>{label}</Text>
        <Text style={styles.commandPanelTitle}>{title}</Text>
        <Text style={styles.commandPanelText}>{value}</Text>
      </View>
      <Image source={{ uri: image }} style={styles.commandMetricImage} resizeMode="cover" />
    </View>
  )
}

function ListPanel({ label, title, body, image, compact }) {
  return (
    <View style={[styles.commandListPanel, compact && styles.panelFull]}>
      <Image source={{ uri: image }} style={styles.commandListImage} resizeMode="cover" />
      <View style={styles.commandListCopy}>
        <Text style={styles.commandListLabel}>{label}</Text>
        <Text style={styles.commandListTitle}>{title}</Text>
        <Text style={styles.commandListText}>{body}</Text>
      </View>
    </View>
  )
}

function SectionFeature({ section, groups = [], compact, copy }) {
  const theme = sectionThemes[section.id] || sectionThemes.profile
  const totalItems = groups.reduce((total, group) => total + (group.totalItems || 0), 0)

  return (
    <View
      style={[
        styles.featurePanel,
        compact && styles.featurePanelCompact,
        { backgroundColor: theme.surface, borderColor: theme.border },
      ]}
    >
      <View style={[styles.featureAccent, compact && styles.featurePanelCompactAccent, { backgroundColor: theme.accent }]} />
      <View style={[styles.featureVisual, compact && styles.featurePanelCompactVisual, { backgroundColor: theme.visual }]}>
        <Image source={{ uri: section.image }} style={styles.featureImage} resizeMode="cover" />
        <View style={[styles.featureVisualTag, { backgroundColor: theme.accent }]}>
          <Text style={styles.featureVisualTagText}>{copy.areaLabel}</Text>
        </View>
      </View>
      <View style={styles.featureCopy}>
        <Text style={[styles.panelLabel, { color: theme.accent }]}>{copy.areaLabel}</Text>
        <Text style={styles.featureTitle}>{section.title}</Text>
        <Text style={styles.featureText}>{section.subtitle}</Text>
        {groups.length > 0 && (
          <View style={styles.featureMetaRow}>
            <View style={[styles.featureMetaDot, { backgroundColor: theme.accent }]} />
            <Text style={styles.featureMetaText}>
              {totalItems} {copy.resources.total}
            </Text>
          </View>
        )}
      </View>
    </View>
  )
}

function WorkspaceHeader({ section, groups, compact, copy }) {
  if (section.id === 'spots') {
    return <AtlasWorkspaceHeader section={section} groups={groups} compact={compact} copy={copy} />
  }

  if (section.id === 'plans') {
    return <MissionWorkspaceHeader section={section} groups={groups} compact={compact} copy={copy} />
  }

  if (section.id === 'session') {
    return <LiveWorkspaceHeader section={section} groups={groups} compact={compact} copy={copy} />
  }

  return <GearWorkspaceHeader section={section} groups={groups} compact={compact} copy={copy} />
}

function AtlasWorkspaceHeader({ section, groups, compact, copy }) {
  const total = groups[0]?.totalItems || 0

  return (
    <View style={[styles.atlasWorkspace, compact && styles.workspaceCompact]}>
      <ImageBackground source={{ uri: lakeSpot }} style={styles.atlasWorkspaceVisual} imageStyle={styles.atlasWorkspaceImage}>
        <View style={styles.atlasWorkspaceOverlay}>
          <Text style={styles.workspaceOverline}>01 / {copy.resources.workspace.fieldAtlas}</Text>
          <Text style={styles.workspaceHeroTitle}>{section.title}</Text>
          <Text style={styles.workspaceHeroText}>{copy.resources.workspace.fieldAtlasText}</Text>
        </View>
      </ImageBackground>
      <View style={styles.atlasWorkspaceInfo}>
        <View style={styles.workspaceInfoHeader}>
          <Text style={styles.workspaceInfoLabel}>{copy.resources.groups.spots}</Text>
          <Text style={styles.workspaceInfoCount}>{total}</Text>
        </View>
        <Text style={styles.workspaceInfoText}>{section.subtitle}</Text>
        <View style={styles.workspaceRule} />
        <View style={styles.workspaceInfoFooter}>
          <Text style={styles.workspaceFooterLabel}>{copy.resources.fields.coordinates}</Text>
          <Text style={styles.workspaceFooterValue}>{copy.resources.workspace.ready}</Text>
        </View>
      </View>
    </View>
  )
}

function MissionWorkspaceHeader({ section, groups, compact, copy }) {
  const total = groups[0]?.totalItems || 0

  return (
    <View style={[styles.missionWorkspace, compact && styles.workspaceCompact]}>
      <View style={styles.missionWorkspaceRail}>
        <Text style={styles.missionWorkspaceCode}>02</Text>
        <View style={styles.missionWorkspaceLine} />
        <Text style={styles.missionWorkspaceCode}>A/B/C</Text>
      </View>
      <View style={styles.missionWorkspaceBody}>
        <View style={styles.missionWorkspaceTop}>
          <View>
            <Text style={styles.workspaceOverline}>{copy.resources.workspace.missionBoard}</Text>
            <Text style={styles.missionWorkspaceTitle}>{section.title}</Text>
          </View>
          <View style={styles.missionWorkspaceCount}>
            <Text style={styles.missionWorkspaceCountValue}>{total}</Text>
            <Text style={styles.missionWorkspaceCountLabel}>{copy.resources.workspace.planned}</Text>
          </View>
        </View>
        <Text style={styles.missionWorkspaceText}>{copy.resources.workspace.missionBoardText}</Text>
        <View style={styles.missionTrack}>
          <MissionTrackPoint label="A" active />
          <View style={styles.missionTrackLine} />
          <MissionTrackPoint label="B" />
          <View style={styles.missionTrackLine} />
          <MissionTrackPoint label="C" />
        </View>
      </View>
    </View>
  )
}

function MissionTrackPoint({ label, active }) {
  return (
    <View style={styles.missionTrackPoint}>
      <View style={[styles.missionTrackDot, active && styles.missionTrackDotActive]}>
        <Text style={[styles.missionTrackDotText, active && styles.missionTrackDotTextActive]}>{label}</Text>
      </View>
    </View>
  )
}

function LiveWorkspaceHeader({ section, groups, compact, copy }) {
  const total = groups[0]?.totalItems || 0

  return (
    <ImageBackground source={{ uri: riverSpot }} style={[styles.liveWorkspace, compact && styles.workspaceCompact]} imageStyle={styles.liveWorkspaceImage}>
      <View style={styles.liveWorkspaceOverlay}>
        <View style={styles.liveWorkspaceTop}>
          <View style={styles.liveSignal}>
            <View style={styles.liveSignalDot} />
            <Text style={styles.liveSignalText}>{copy.resources.workspace.liveConsole}</Text>
          </View>
          <Text style={styles.liveSessionCount}>{total} {copy.resources.total}</Text>
        </View>
        <Text style={styles.liveWorkspaceTitle}>{section.title}</Text>
        <Text style={styles.liveWorkspaceText}>{copy.resources.workspace.liveConsoleText}</Text>
        <View style={styles.livePulseRow}>
          <View style={[styles.livePulseBar, styles.livePulseShort]} />
          <View style={[styles.livePulseBar, styles.livePulseTall]} />
          <View style={[styles.livePulseBar, styles.livePulseMedium]} />
          <View style={[styles.livePulseBar, styles.livePulseShort]} />
          <Text style={styles.livePulseLabel}>{copy.resources.status}</Text>
        </View>
      </View>
    </ImageBackground>
  )
}

function GearWorkspaceHeader({ section, groups, compact, copy }) {
  const total = groups[0]?.totalItems || 0

  return (
    <View style={[styles.gearWorkspace, compact && styles.workspaceCompact]}>
      <View style={styles.gearWorkspaceImageFrame}>
        <Image source={{ uri: spinnerbait }} style={styles.gearWorkspaceImage} resizeMode="cover" />
      </View>
      <View style={styles.gearWorkspaceBody}>
        <Text style={styles.workspaceOverline}>{copy.resources.workspace.gearInventory}</Text>
        <Text style={styles.gearWorkspaceTitle}>{section.title}</Text>
        <Text style={styles.gearWorkspaceText}>{copy.resources.workspace.gearInventoryText}</Text>
        <View style={styles.gearWorkspaceShelf}>
          <GearShelfValue label={copy.resources.groups.lureBox} value={total} />
          <GearShelfValue label={copy.dashboard.lures} value={copy.resources.workspace.ready} />
          <GearShelfValue label={copy.resources.fields.active} value={copy.resources.yes} />
        </View>
      </View>
    </View>
  )
}

function GearShelfValue({ label, value }) {
  return (
    <View style={styles.gearShelfValue}>
      <Text style={styles.gearShelfLabel}>{label}</Text>
      <Text style={styles.gearShelfNumber}>{value}</Text>
    </View>
  )
}

function ProfileWorkspaceHeader({ compact, copy }) {
  return (
    <View style={[styles.profileWorkspace, compact && styles.profileWorkspaceCompact]}>
      <View style={styles.profileWorkspaceIdentity}>
        <View style={styles.profileWorkspaceAvatarFrame}>
          <Image source={{ uri: profileIcon }} style={styles.profileWorkspaceAvatar} resizeMode="cover" />
        </View>
        <View style={styles.profileWorkspaceCopy}>
          <Text style={styles.profileWorkspaceOverline}>{copy.resources.profileReady}</Text>
          <Text style={styles.profileWorkspaceTitle}>{copy.menu.profile}</Text>
          <Text style={styles.profileWorkspaceText}>{copy.resources.profileBody}</Text>
        </View>
      </View>
      <View style={styles.profileWorkspaceStatus}>
        <View style={styles.profileStatusHeader}>
          <Text style={styles.profileStatusLabel}>{copy.resources.noAuth}</Text>
          <Text style={styles.profileStatusValue}>MVP</Text>
        </View>
        <View style={styles.profileStatusRule} />
        <Text style={styles.profileStatusHint}>{copy.resources.profileReady}</Text>
      </View>
    </View>
  )
}

function GalleryShowcase({
  groups,
  loading,
  error,
  detail,
  onOpenDetail,
  onCloseDetail,
  onSearchChange,
  onPageChange,
  compact,
  search,
  onCreated,
  copy,
}) {
  const group = groups.find((candidate) => candidate.key === 'catches') || groups[0]
  const items = group?.items || []
  const [editorState, setEditorState] = useState(null)
  const [feedback, setFeedback] = useState(null)

  function openCreateForm() {
    setFeedback(null)
    setEditorState({ item: null })
    onCloseDetail()
  }

  function openEditForm(item) {
    setFeedback(null)
    setEditorState({ item })
    onCloseDetail()
  }

  async function deleteCatch(item) {
    if (!item?.sessionId || !item?.catchId) {
      return
    }

    if (typeof window !== 'undefined' && !window.confirm(copy.resources.catchDeleteConfirm)) {
      return
    }

    try {
      const response = await fetch(`/api/sessions/${item.sessionId}/catches/${item.catchId}`, { method: 'DELETE' })

      if (!response.ok) {
        throw new Error('Delete catch failed')
      }

      setFeedback({ type: 'success', text: copy.resources.catchDeleted })
      onCloseDetail()
      onCreated()
    } catch {
      setFeedback({ type: 'error', text: copy.resources.catchDeleteError })
    }
  }

  return (
    <View style={[styles.galleryScreen, compact && styles.galleryScreenCompact]}>
      <View style={[styles.galleryShowcaseHeader, compact && styles.galleryShowcaseHeaderCompact]}>
        <View style={styles.galleryHeaderCopy}>
          <Text style={styles.galleryShowcaseEyebrow}>{copy.resources.galleryImages}</Text>
          <Text style={styles.galleryShowcaseTitle}>{copy.sections.gallery.title}</Text>
          <Text style={styles.galleryShowcaseSubtitle}>{copy.sections.gallery.subtitle}</Text>
          <View style={styles.galleryHeaderMetaRow}>
            <View style={styles.galleryHeaderMetaDot} />
            <Text style={styles.galleryHeaderMetaText}>{group?.totalItems ?? items.length} {copy.resources.galleryCountLabel}</Text>
            <Text style={styles.galleryHeaderMetaDivider}>/</Text>
            <Text style={styles.galleryHeaderMetaText}>{copy.resources.galleryMemory}</Text>
          </View>
        </View>
        <View style={styles.galleryHeaderVisual}>
          <View style={styles.galleryHeaderVisualStripe} />
          <View style={styles.galleryHeaderVisualBlock} />
          <Image source={{ uri: galleryIcon }} style={styles.galleryHeaderVisualIcon} resizeMode="contain" />
          <View style={styles.galleryHeaderVisualCaption}>
            <Text style={styles.galleryHeaderVisualNumber}>{String(group?.totalItems ?? items.length).padStart(2, '0')}</Text>
            <Text style={styles.galleryHeaderVisualLabel}>{copy.resources.galleryImages}</Text>
          </View>
        </View>
      </View>

      <View style={styles.galleryCaptureActionRow}>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={copy.resources.newCatch}
          onPress={openCreateForm}
          style={styles.galleryCreateButton}
        >
          <Text style={styles.galleryCreateButtonMark}>+</Text>
          <Text style={styles.galleryCreateButtonText}>{copy.resources.newCatch}</Text>
        </Pressable>
      </View>

      {loading && (
        <View style={styles.loadingLine}>
          <ActivityIndicator color="#e66f51" />
          <Text style={styles.loadingText}>{copy.loading}</Text>
        </View>
      )}

      {error && (
        <View style={styles.notice}>
          <Text style={styles.noticeText}>{copy.resources.loadError}</Text>
        </View>
      )}

      {feedback && (
        <View style={[styles.formFeedback, feedback.type === 'success' && styles.formFeedbackSuccess]}>
          <Text style={[styles.formFeedbackText, feedback.type === 'success' && styles.formFeedbackTextSuccess]}>{feedback.text}</Text>
        </View>
      )}

      {editorState && (
        <GalleryCatchForm
          key={editorState.item?.catchId || 'new-catch'}
          item={editorState.item}
          copy={copy}
          compact={compact}
          onSaved={() => {
            setEditorState(null)
            setFeedback({ type: 'success', text: copy.resources.catchSaved })
            onCreated()
          }}
          onCancel={() => setEditorState(null)}
        />
      )}

      {detail?.item && (
        <DetailPanel
          detail={detail}
          onClose={onCloseDetail}
          onEditItem={() => openEditForm(detail.item)}
          onDeleteItem={() => deleteCatch(detail.item)}
          copy={copy}
          compact={compact}
        />
      )}

      {items.length > 0 ? (
        <View style={[styles.galleryGrid, compact && styles.galleryGridCompact]}>
          {items.map((item, index) => (
            <GalleryTile
              key={`gallery-${getItemId(item, 'catches')}`}
              item={item}
              index={index}
              copy={copy}
              onPress={() => onOpenDetail(item, group)}
            />
          ))}
        </View>
      ) : (
        <View style={styles.galleryEmpty}>
          <Image source={{ uri: galleryIcon }} style={styles.galleryEmptyIcon} />
          <Text style={styles.galleryEmptyTitle}>{copy.resources.empty}</Text>
          <Text style={styles.galleryEmptyText}>{copy.sections.gallery.subtitle}</Text>
        </View>
      )}
    </View>
  )
}

function GalleryCatchForm({ item, copy, compact, onSaved, onCancel }) {
  const fields = copy.resources.fields
  const [form, setForm] = useState(() => ({
    sessionId: item?.sessionId ? String(item.sessionId) : '',
    species: item?.species || '',
    lureLibraryItemId: item?.lureLibraryItemId ? String(item.lureLibraryItemId) : '',
    sizeCm: item?.sizeCm ?? '',
    weightKg: item?.weightKg ?? '',
    photoUrl: item?.photoUrl || '',
    photoCaption: item?.photoCaption || '',
  }))
  const [fishOptions, setFishOptions] = useState([])
  const [lureOptions, setLureOptions] = useState([])
  const [sessionOptions, setSessionOptions] = useState([])
  const [loadingOptions, setLoadingOptions] = useState(true)
  const [saving, setSaving] = useState(false)
  const [feedback, setFeedback] = useState(null)

  useEffect(() => {
    let ignore = false

    Promise.all([
      fetch('/api/fish?page=0&size=100&sortBy=name&sortDirection=asc').then((response) => response.json()),
      fetch('/api/lure-library?page=0&size=100&sortBy=name&sortDirection=asc').then((response) => response.json()),
      fetch('/api/sessions?page=0&size=100&sortBy=date&sortDirection=desc').then((response) => response.json()),
    ])
      .then(([fishPage, lurePage, sessionPage]) => {
        if (ignore) {
          return
        }

        setFishOptions(Array.isArray(fishPage.items) ? fishPage.items : [])
        setLureOptions(Array.isArray(lurePage.items) ? lurePage.items : [])
        setSessionOptions(Array.isArray(sessionPage.items) ? sessionPage.items : [])
      })
      .catch(() => {
        if (!ignore) {
          setFeedback({ type: 'error', text: copy.resources.catchSaveError })
        }
      })
      .finally(() => {
        if (!ignore) {
          setLoadingOptions(false)
        }
      })

    return () => {
      ignore = true
    }
  }, [copy.resources.catchSaveError])

  function updateField(field, value) {
    setFeedback(null)
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function selectPhoto() {
    const imageData = await readImageFromDevice()

    if (imageData) {
      updateField('photoUrl', imageData)
    }
  }

  async function submit() {
    const selectedFish = fishOptions.find((option) => option.name?.toLowerCase() === form.species.toLowerCase())
    const selectedSession = sessionOptions.find((option) => String(option.id) === String(form.sessionId))

    if (!selectedFish || !selectedSession) {
      setFeedback({
        type: 'error',
        text: !selectedFish ? copy.resources.noFishAvailable : copy.resources.noSessionsAvailable,
      })
      return
    }

    setSaving(true)
    setFeedback(null)

    const payload = {
      species: selectedFish.name,
      lureLibraryItemId: form.lureLibraryItemId ? Number(form.lureLibraryItemId) : null,
      quantity: 1,
      sizeCm: toNullableNumber(form.sizeCm),
      weightKg: toNullableNumber(form.weightKg),
      released: true,
      photoUrl: toNullableText(form.photoUrl),
      photoThumbnailUrl: toNullableText(form.photoUrl),
      photoCaption: toNullableText(form.photoCaption),
    }

    try {
      const endpoint = item?.catchId
        ? `/api/sessions/${selectedSession.id}/catches/${item.catchId}`
        : `/api/sessions/${selectedSession.id}/catches`
      const response = await fetch(endpoint, {
        method: item?.catchId ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })

      if (!response.ok) {
        throw new Error('Save catch failed')
      }

      onSaved()
    } catch {
      setFeedback({ type: 'error', text: copy.resources.catchSaveError })
    } finally {
      setSaving(false)
    }
  }

  const selectedFish = fishOptions.find((option) => option.name?.toLowerCase() === form.species.toLowerCase())
  const selectedLure = lureOptions.find((option) => String(option.id) === String(form.lureLibraryItemId))
  const fishSelectOptions = fishOptions.map((option) => ({
    value: option.name,
    label: option.name,
    image: getImageSource(option.imageUrl, getFishImage(option.name)),
  }))
  const lureSelectOptions = lureOptions.map((option) => ({
    value: String(option.id),
    label: option.name,
    image: getImageSource(option.imageUrl, getLureImage(option.name)),
  }))
  const sessionSelectOptions = sessionOptions.map((option) => ({
    value: String(option.id),
    label: `${option.date || copy.undefinedDate} · ${option.startTime || ''} · ${option.spotName || copy.dashboard.unnamedSpot}`,
  }))
  const fishPreview = getImageSource(selectedFish?.imageUrl, getFishImage(form.species))
  const lurePreview = selectedLure ? getImageSource(selectedLure.imageUrl, getLureImage(selectedLure.name)) : galleryIcon

  return (
    <View style={[styles.galleryEditor, compact && styles.galleryEditorCompact]}>
      <View style={styles.galleryEditorHeader}>
        <View>
          <Text style={styles.galleryEditorEyebrow}>{copy.resources.groups.catches}</Text>
          <Text style={styles.galleryEditorTitle}>{item?.catchId ? copy.resources.editCatch : copy.resources.newCatch}</Text>
          <Text style={styles.galleryEditorHint}>{copy.resources.catchEditorHint}</Text>
        </View>
        <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.cancel} onPress={onCancel} style={styles.galleryEditorCancel}>
          <Text style={styles.galleryEditorCancelText}>{copy.resources.cancel}</Text>
        </Pressable>
      </View>

      <View style={styles.galleryEditorPreviewRow}>
        <View style={styles.galleryEditorFishPreview}>
          <Image source={{ uri: form.photoUrl || fishPreview }} style={styles.galleryEditorFishImage} resizeMode="cover" />
          <View style={styles.galleryEditorPreviewOverlay}>
            <Text style={styles.galleryEditorPreviewLabel}>{fields.photo}</Text>
          </View>
        </View>
        <View style={styles.galleryEditorLurePreview}>
          <Image source={{ uri: lurePreview }} style={styles.galleryEditorLureImage} resizeMode="contain" />
          <Text style={styles.galleryEditorLureLabel}>{selectedLure?.name || copy.resources.noLureSelected}</Text>
        </View>
      </View>

      <View style={styles.galleryFormGrid}>
        <GallerySelect
          label={copy.resources.chooseSession}
          value={form.sessionId}
          options={sessionSelectOptions}
          onChange={(value) => updateField('sessionId', value)}
          placeholder={loadingOptions ? copy.loading : copy.resources.chooseSession}
        />
        <GallerySelect
          label={copy.resources.chooseSpecies}
          value={form.species}
          options={fishSelectOptions}
          onChange={(value) => updateField('species', value)}
          placeholder={loadingOptions ? copy.loading : copy.resources.chooseSpecies}
        />
        <GallerySelect
          label={copy.resources.chooseLure}
          value={form.lureLibraryItemId}
          options={lureSelectOptions}
          onChange={(value) => updateField('lureLibraryItemId', value)}
          placeholder={loadingOptions ? copy.loading : copy.resources.noLureSelected}
          allowEmpty
        />
        <FormField label={fields.size} value={String(form.sizeCm)} onChangeText={(value) => updateField('sizeCm', value)} keyboardType="numeric" />
        <FormField label={fields.weight} value={String(form.weightKg)} onChangeText={(value) => updateField('weightKg', value)} keyboardType="numeric" />
        <FormField label={fields.notes} value={form.photoCaption} onChangeText={(value) => updateField('photoCaption', value)} multiline />
      </View>

      <LibraryImagePicker
        label={copy.resources.chooseCatchPhoto}
        hint={form.photoUrl ? copy.resources.catchImageSelected : copy.resources.catchPhotoHint}
        value={form.photoUrl}
        preview={form.photoUrl || fishPreview}
        onPress={selectPhoto}
        copy={copy}
        style={styles.galleryImagePicker}
      />

      {feedback && (
        <View style={styles.formFeedback}>
          <Text style={styles.formFeedbackText}>{feedback.text}</Text>
        </View>
      )}

      <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.saveCatch} disabled={saving} onPress={submit} style={[styles.submitButton, saving && styles.submitButtonDisabled]}>
        <Text style={styles.submitButtonText}>{saving ? copy.resources.saving : copy.resources.saveCatch}</Text>
      </Pressable>
    </View>
  )
}

function GallerySelect({ label, value, options, onChange, placeholder, allowEmpty = false }) {
  const [open, setOpen] = useState(false)
  const selected = options.find((option) => String(option.value) === String(value))

  return (
    <View style={styles.gallerySelect}>
      <Text style={styles.formLabel}>{label}</Text>
      <Pressable
        accessibilityRole="combobox"
        accessibilityLabel={label}
        accessibilityState={{ expanded: open, selected: Boolean(selected) }}
        onPress={() => setOpen((current) => !current)}
        style={[styles.gallerySelectButton, open && styles.gallerySelectButtonOpen]}
      >
        {selected?.image && <Image source={{ uri: selected.image }} style={styles.gallerySelectSelectedImage} resizeMode="contain" />}
        <Text style={[styles.gallerySelectButtonText, !selected && styles.gallerySelectPlaceholder]}>{selected?.label || placeholder}</Text>
        <Text style={styles.gallerySelectButtonIcon}>{open ? '-' : '+'}</Text>
      </Pressable>
      {open && (
        <View style={styles.gallerySelectMenu}>
          {allowEmpty && (
            <Pressable
              accessibilityRole="option"
              accessibilityLabel={placeholder}
              onPress={() => {
                onChange('')
                setOpen(false)
              }}
              style={styles.gallerySelectOption}
            >
              <Text style={styles.gallerySelectOptionText}>{placeholder}</Text>
            </Pressable>
          )}
          {options.length > 0 ? options.map((option) => {
            const isSelected = String(option.value) === String(value)

            return (
              <Pressable
                key={option.value}
                accessibilityRole="option"
                accessibilityLabel={option.label}
                accessibilityState={{ selected: isSelected }}
                onPress={() => {
                  onChange(option.value)
                  setOpen(false)
                }}
                style={[styles.gallerySelectOption, isSelected && styles.gallerySelectOptionSelected]}
              >
                {option.image && <Image source={{ uri: option.image }} style={styles.gallerySelectOptionImage} resizeMode="contain" />}
                <Text style={[styles.gallerySelectOptionText, isSelected && styles.gallerySelectOptionTextSelected]}>{option.label}</Text>
              </Pressable>
            )
          }) : (
            <Text style={styles.gallerySelectEmpty}>{placeholder}</Text>
          )}
        </View>
      )}
    </View>
  )
}

function GalleryTile({ item, index, copy, onPress }) {
  const display = getItemDisplay(item, 'catches', copy)
  const location = item.spotName || copy.dashboard.unnamedSpot
  const date = item.sessionDate || copy.undefinedDate
  const measure = compactLine(formatCatchSize(item), formatCatchWeight(item))
  const accentStyles = [styles.galleryTileAccentCoral, styles.galleryTileAccentTeal, styles.galleryTileAccentGold, styles.galleryTileAccentViolet]

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`${copy.resources.viewDetails}: ${display.title}`}
      onPress={onPress}
      style={[styles.galleryTile, accentStyles[index % accentStyles.length]]}
    >
      <View style={styles.galleryTileImageFrame}>
        <Image source={{ uri: display.image || galleryIcon }} style={styles.galleryTileImage} resizeMode="cover" />
        <View style={styles.galleryTileIndexBadge}>
          <Text style={styles.galleryTileIndexText}>{String(index + 1).padStart(2, '0')}</Text>
        </View>
        {item.lureName && (
          <View style={styles.galleryTileLureBadge}>
            <Image source={{ uri: getImageSource(item.lureImageUrl, getLureImage(item.lureName)) }} style={styles.galleryTileLureImage} resizeMode="contain" />
            <Text style={styles.galleryTileLureText}>{item.lureName}</Text>
          </View>
        )}
        <View style={styles.galleryTileOverlay}>
          <Text style={styles.galleryTileOverlayText}>{copy.resources.viewDetails}</Text>
        </View>
      </View>
      <View style={styles.galleryTileCaption}>
        <View style={styles.galleryTileTitleRow}>
          <Text style={styles.galleryTileTitle}>{display.title}</Text>
          <Text style={styles.galleryTileArrow}>&gt;</Text>
        </View>
        <View style={styles.galleryTileLocationRow}>
          <View style={styles.galleryTileLocationDot} />
          <Text style={styles.galleryTileMeta}>{location}</Text>
        </View>
        <View style={styles.galleryTileFooter}>
          <Text style={styles.galleryTileDate}>{date}</Text>
          {measure !== '-' && <Text style={styles.galleryTileMeasure}>{measure}</Text>}
        </View>
      </View>
    </Pressable>
  )
}

function LibraryShowcase({
  groups,
  loading,
  error,
  detail,
  onOpenDetail,
  onCloseDetail,
  onPageChange,
  compact,
  waterEnvironment,
  onWaterEnvironmentChange,
  onCreated,
  copy,
}) {
  const [libraryType, setLibraryType] = useState('fish')
  const [editorState, setEditorState] = useState(null)
  const [libraryFeedback, setLibraryFeedback] = useState(null)
  const selectedKey = libraryType === 'fish' ? 'fish' : 'lureLibrary'
  const selectedGroup = groups.find((group) => group.key === selectedKey)
  const items = selectedGroup?.items || []
  const title = libraryType === 'fish' ? copy.resources.groups.fish : copy.resources.groups.lureLibrary
  const subtitle = libraryType === 'fish' ? copy.resources.libraryFishHint : copy.resources.libraryLureHint

  function openCreateForm() {
    const groupKey = libraryType === 'fish' ? 'fish' : 'lureLibrary'
    setLibraryFeedback(null)
    setEditorState({ groupKey, item: null })
    onCloseDetail()
  }

  function openEditForm(item, group) {
    setLibraryFeedback(null)
    setEditorState({ groupKey: group.key, item })
    onCloseDetail()
  }

  function changeLibraryType(nextType) {
    setLibraryType(nextType)
    setEditorState(null)
    setLibraryFeedback(null)
    onCloseDetail()
  }

  async function deleteItem(item, group) {
    if (typeof window !== 'undefined' && !window.confirm(copy.resources.deleteConfirm)) {
      return
    }

    try {
      const response = await fetch(`/api/${group.key === 'fish' ? 'fish' : 'lure-library'}/${getItemId(item, group.key)}`, {
        method: 'DELETE',
      })

      if (!response.ok) {
        throw new Error('Delete library entry failed')
      }

      setLibraryFeedback({ type: 'success', text: copy.resources.deleteEntrySuccess })
      onCloseDetail()
      onCreated()
    } catch {
      setLibraryFeedback({ type: 'error', text: copy.resources.deleteEntryError })
    }
  }

  return (
    <View style={[styles.libraryScreen, compact && styles.libraryScreenCompact]}>
      <View style={styles.libraryShowcaseHeader}>
        <Text style={styles.libraryShowcaseTitle}>{copy.sections.library.title}</Text>
        <Text style={styles.libraryShowcaseSubtitle}>{subtitle}</Text>
        <View style={styles.librarySwitch}>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.resources.groups.fish}
            onPress={() => changeLibraryType('fish')}
            style={[styles.librarySwitchButton, libraryType === 'fish' && styles.librarySwitchButtonSelected]}
          >
            <Text style={[styles.librarySwitchText, libraryType === 'fish' && styles.librarySwitchTextSelected]}>
              {copy.resources.groups.fish}
            </Text>
          </Pressable>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.resources.groups.lureLibrary}
            onPress={() => changeLibraryType('lures')}
            style={[styles.librarySwitchButton, libraryType === 'lures' && styles.librarySwitchButtonSelected]}
          >
            <Text style={[styles.librarySwitchText, libraryType === 'lures' && styles.librarySwitchTextSelected]}>
              {copy.resources.groups.lureLibrary}
            </Text>
          </Pressable>
        </View>
        {libraryType === 'fish' && (
          <View style={styles.libraryEnvironmentFilter}>
            <Text style={styles.libraryEnvironmentLabel}>{copy.resources.waterEnvironment}</Text>
            <View style={styles.libraryEnvironmentOptions}>
              {[
                { value: 'ALL', label: copy.resources.allEnvironments },
                { value: 'FRESHWATER', label: copy.resources.freshwater },
                { value: 'SALTWATER', label: copy.resources.saltwater },
              ].map((option) => {
                const selected = waterEnvironment === option.value

                return (
                  <Pressable
                    key={option.value}
                    accessibilityRole="button"
                    accessibilityLabel={option.label}
                    accessibilityState={{ selected }}
                    onPress={() => onWaterEnvironmentChange(option.value)}
                    style={[
                      styles.libraryEnvironmentOption,
                      selected && styles.libraryEnvironmentOptionSelected,
                      option.value === 'FRESHWATER' && selected && styles.libraryEnvironmentFreshwaterSelected,
                      option.value === 'SALTWATER' && selected && styles.libraryEnvironmentSaltwaterSelected,
                    ]}
                  >
                    <Text style={[styles.libraryEnvironmentOptionText, selected && styles.libraryEnvironmentOptionTextSelected]}>
                      {option.label}
                    </Text>
                  </Pressable>
                )
              })}
            </View>
          </View>
        )}
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={libraryType === 'fish' ? copy.resources.newFish : copy.resources.newLure}
          onPress={openCreateForm}
          style={styles.libraryCreateButton}
        >
          <Text style={styles.libraryCreateButtonText}>
            + {libraryType === 'fish' ? copy.resources.newFish : copy.resources.newLure}
          </Text>
        </Pressable>
      </View>

      {selectedGroup && (
        <View style={[styles.libraryToolbar, compact && styles.libraryToolbarCompact]}>
          <PaginationControls group={selectedGroup} onPageChange={onPageChange} copy={copy} />
        </View>
      )}

      {loading && (
        <View style={styles.loadingLine}>
          <ActivityIndicator color="#2d8f72" />
          <Text style={styles.loadingText}>{copy.loading}</Text>
        </View>
      )}

      {error && (
        <View style={styles.notice}>
          <Text style={styles.noticeText}>{copy.resources.loadError}</Text>
        </View>
      )}

      {libraryFeedback && (
        <View style={[styles.formFeedback, libraryFeedback.type === 'success' && styles.formFeedbackSuccess]}>
          <Text style={[styles.formFeedbackText, libraryFeedback.type === 'success' && styles.formFeedbackTextSuccess]}>
            {libraryFeedback.text}
          </Text>
        </View>
      )}

      {editorState && (
        <LibraryEditorForm
          key={`${editorState.groupKey}-${editorState.item?.id || 'new'}`}
          groupKey={editorState.groupKey}
          item={editorState.item}
          copy={copy}
          compact={compact}
          onSaved={() => {
            setEditorState(null)
            setLibraryFeedback({ type: 'success', text: copy.resources.saveEntrySuccess })
            onCreated()
          }}
          onCancel={() => setEditorState(null)}
        />
      )}

      {detail?.item && (
        <DetailPanel
          detail={detail}
          onClose={onCloseDetail}
          onEditItem={() => openEditForm(detail.data || detail.item, detail.group)}
          onDeleteItem={() => deleteItem(detail.data || detail.item, detail.group)}
          copy={copy}
          compact={compact}
        />
      )}

      {items.length > 0 ? (
        <View style={[styles.libraryGrid, compact && styles.libraryGridCompact]}>
          {items.map((item) => (
            <LibraryTile
              key={`library-${selectedKey}-${getItemId(item, selectedKey)}`}
              item={item}
              group={selectedGroup}
              copy={copy}
              compact={compact}
              onPress={() => onOpenDetail(item, selectedGroup)}
            />
          ))}
        </View>
      ) : (
        <View style={styles.libraryEmpty}>
          <Image source={{ uri: selectedGroup?.image || libraryIcon }} style={styles.libraryEmptyIcon} />
          <Text style={styles.libraryEmptyTitle}>{copy.resources.empty}</Text>
          <Text style={styles.libraryEmptyText}>{title}</Text>
        </View>
      )}
    </View>
  )
}

function LibraryTile({ item, group, copy, compact, onPress }) {
  const display = getItemDisplay(item, group.key, copy)
  const isFish = group.key === 'fish'
  const meta = isFish
    ? compactLine(item.habitatNotes, item.activeTimes)
    : compactLine(item.type, item.difficulty)
  const detail = isFish
    ? compactLine(
        formatOptionList(item.strikeZone, getFishStrikeZoneOptions(copy)),
        formatOptionList(item.commonZones, getFishCommonZoneOptions(copy)),
      )
    : compactLine(item.actionType, item.idealConditions)
  const tag = isFish ? formatListText(item.favoriteLures) : item.effectiveness
  const environment = isFish ? normalizeWaterEnvironment(item.waterEnvironment) : null

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`${copy.resources.viewDetails}: ${display.title}`}
      onPress={onPress}
      style={[styles.libraryTile, compact && styles.libraryTileCompact, isFish ? styles.libraryFishTile : styles.libraryLureTile]}
    >
      <View style={[styles.libraryTileImageFrame, isFish ? styles.libraryFishImageFrame : styles.libraryLureImageFrame]}>
        <Image source={{ uri: display.image || group.image }} style={styles.libraryTileImage} resizeMode="contain" />
      </View>
      <View style={styles.libraryTileBody}>
        <Text style={styles.libraryTileTitle}>{display.title}</Text>
        {isFish && (
          <FishEnvironmentBadge environment={environment} copy={copy} />
        )}
        <Text style={styles.libraryTileMeta}>{meta || (isFish ? copy.resources.groups.fish : copy.resources.groups.lureLibrary)}</Text>
        {detail && <Text style={styles.libraryTileDetail}>{detail}</Text>}
        {tag && (
          <View style={[styles.libraryTileTag, isFish ? styles.libraryFishTag : styles.libraryLureTag]}>
            <Text style={styles.libraryTileTagText}>{tag}</Text>
          </View>
        )}
        <Text style={[styles.libraryTileAction, { color: isFish ? '#2d8f72' : '#3978bb' }]}>
          {copy.resources.viewDetails}
        </Text>
      </View>
    </Pressable>
  )
}

function LibraryEditorForm({ groupKey, item, copy, compact, onSaved, onCancel }) {
  const isFish = groupKey === 'fish'
  const fields = copy.resources.fields
  const actionOptions = getLureActionOptions({}, copy)
  const [form, setForm] = useState(() => ({
    name: item?.name || '',
    waterEnvironment: item?.waterEnvironment || '',
    type: item?.type || '',
    imageUrl: item?.imageUrl || '',
    difficulty: item?.difficulty || '',
    effectiveness: item?.effectiveness || '',
    description: item?.description || '',
    usageNotes: item?.usageNotes || '',
    actionType: item?.actionType || '',
    idealConditions: item?.idealConditions || '',
    actionIconUrl: item?.actionIconUrl || '',
    actionImageUrl: item?.actionImageUrl || '',
    habitatNotes: item?.habitatNotes || '',
    activeTimes: item?.activeTimes || '',
    strikeZone: parseListValue(item?.strikeZone),
    commonZones: parseListValue(item?.commonZones),
    favoriteLures: parseListValue(item?.favoriteLures),
  }))
  const [saving, setSaving] = useState(false)
  const [feedback, setFeedback] = useState(null)
  const [availableLures, setAvailableLures] = useState([])
  const [luresLoading, setLuresLoading] = useState(false)

  useEffect(() => {
    if (!isFish) {
      return undefined
    }

    let ignore = false
    setLuresLoading(true)

    fetch('/api/lure-library?page=0&size=100&sortBy=name&sortDirection=asc')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Lure library unavailable')
        }

        return response.json()
      })
      .then((payload) => {
        if (!ignore) {
          setAvailableLures(Array.isArray(payload) ? payload : payload.items || [])
        }
      })
      .catch(() => {
        if (!ignore) {
          setAvailableLures([])
        }
      })
      .finally(() => {
        if (!ignore) {
          setLuresLoading(false)
        }
      })

    return () => {
      ignore = true
    }
  }, [isFish])

  function updateField(field, value) {
    setFeedback(null)
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function selectImage(field) {
    const imageData = await readImageFromDevice()

    if (imageData) {
      updateField(field, imageData)
    }
  }

  function selectAction(action) {
    setFeedback(null)
    setForm((current) => ({
      ...current,
      actionType: action.label,
      actionIconUrl: action.icon,
      actionImageUrl: action.image,
    }))
  }

  async function submit() {
    if (!form.name.trim() || (isFish && !form.waterEnvironment) || (!isFish && !form.type.trim())) {
      const message = isFish && !form.waterEnvironment ? copy.resources.requiredWaterEnvironment : copy.resources.requiredLibraryFields
      setFeedback({ type: 'error', text: message })
      return
    }

    setSaving(true)
    setFeedback(null)

    const endpoint = isFish ? 'fish' : 'lure-library'
    const payload = isFish
        ? {
            name: form.name.trim(),
            waterEnvironment: form.waterEnvironment,
            description: toNullableText(form.description),
          imageUrl: toNullableText(form.imageUrl),
          habitatNotes: toNullableText(form.habitatNotes),
          activeTimes: toNullableText(form.activeTimes),
          strikeZone: toNullableText(form.strikeZone.join(', ')),
          commonZones: toNullableText(form.commonZones.join(', ')),
          favoriteLures: toNullableText(form.favoriteLures.join(', ')),
        }
      : {
          name: form.name.trim(),
          type: form.type.trim(),
          imageUrl: toNullableText(form.imageUrl),
          difficulty: toNullableText(form.difficulty),
          effectiveness: toNullableText(form.effectiveness),
          description: toNullableText(form.description),
          usageNotes: toNullableText(form.usageNotes),
          actionType: toNullableText(form.actionType),
          idealConditions: toNullableText(form.idealConditions),
          actionIconUrl: toNullableText(form.actionIconUrl),
          actionImageUrl: toNullableText(form.actionImageUrl),
        }

    try {
      const response = await fetch(`/api/${endpoint}${item?.id ? `/${item.id}` : ''}`, {
        method: item?.id ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      })

      if (!response.ok) {
        throw new Error('Save library entry failed')
      }

      onSaved()
    } catch {
      setFeedback({ type: 'error', text: copy.resources.saveEntryError })
    } finally {
      setSaving(false)
    }
  }

  const imagePreview = form.imageUrl || (isFish ? getFishImage(form.name) : getLureImage(form.name))
  const favoriteLureOptions = getFavoriteLureOptions(availableLures, form.favoriteLures)

  return (
    <View style={[styles.libraryEditor, compact && styles.libraryEditorCompact]}>
      <View style={styles.libraryEditorHeader}>
        <View>
          <Text style={styles.libraryEditorEyebrow}>{isFish ? copy.resources.groups.fish : copy.resources.groups.lureLibrary}</Text>
          <Text style={styles.libraryEditorTitle}>{item?.id ? copy.resources.editEntry : isFish ? copy.resources.newFish : copy.resources.newLure}</Text>
        </View>
        <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.cancel} onPress={onCancel} style={styles.libraryEditorCancel}>
          <Text style={styles.libraryEditorCancelText}>{copy.resources.cancel}</Text>
        </Pressable>
      </View>

      <View style={styles.libraryEditorMain}>
        <View style={styles.libraryEditorPreviewFrame}>
          <Image source={{ uri: imagePreview }} style={styles.libraryEditorPreviewImage} resizeMode="contain" />
        </View>
        <View style={styles.libraryEditorFields}>
          <FormField label={fields.name} value={form.name} onChangeText={(value) => updateField('name', value)} fieldStyle={styles.libraryEditorField} />
          {!isFish && <FormField label={fields.type} value={form.type} onChangeText={(value) => updateField('type', value)} fieldStyle={styles.libraryEditorField} />}
          <FormField label={fields.description} value={form.description} onChangeText={(value) => updateField('description', value)} fieldStyle={styles.libraryEditorField} multiline />
          <LibraryImagePicker
            label={copy.resources.chooseImage}
            hint={copy.resources.customImageHint}
            value={form.imageUrl}
            preview={imagePreview}
            onPress={() => selectImage('imageUrl')}
            copy={copy}
            standalone
          />
        </View>
      </View>

      {isFish ? (
        <View style={styles.libraryEditorSection}>
          <Text style={styles.libraryEditorSectionTitle}>{copy.resources.groups.fish}</Text>
          <View style={styles.formGrid}>
            <FormField label={fields.habitat} value={form.habitatNotes} onChangeText={(value) => updateField('habitatNotes', value)} multiline />
            <FormField label={fields.activeTimes} value={form.activeTimes} onChangeText={(value) => updateField('activeTimes', value)} multiline />
          </View>
          <FishEnvironmentField value={form.waterEnvironment} onChange={(value) => updateField('waterEnvironment', value)} copy={copy} />
          <View style={styles.libraryMultiSelectGrid}>
            <MultiSelectCombo
              label={fields.strikeZone}
              values={form.strikeZone}
              options={getFishStrikeZoneOptions(copy)}
              onChange={(value) => updateField('strikeZone', value)}
              copy={copy}
            />
            <MultiSelectCombo
              label={fields.zones}
              values={form.commonZones}
              options={getFishCommonZoneOptions(copy)}
              onChange={(value) => updateField('commonZones', value)}
              copy={copy}
            />
            <MultiSelectCombo
              label={fields.favoriteLures}
              values={form.favoriteLures}
              options={favoriteLureOptions}
              onChange={(value) => updateField('favoriteLures', value)}
              hint={luresLoading ? copy.loading : copy.resources.selectMultiple}
              emptyText={copy.resources.noLuresAvailable}
              copy={copy}
            />
          </View>
        </View>
      ) : (
        <View style={styles.libraryEditorSection}>
          <View style={styles.libraryEditorSectionHeader}>
            <View>
              <Text style={styles.libraryEditorSectionTitle}>{copy.resources.actionGuideTitle}</Text>
              <Text style={styles.libraryEditorSectionHint}>{copy.resources.manualActionHint}</Text>
            </View>
          </View>
          <View style={styles.libraryActionPresetGrid}>
            {actionOptions.map((action) => {
              const selected = form.actionIconUrl === action.icon && form.actionImageUrl === action.image

              return (
                <Pressable
                  key={action.id}
                  accessibilityRole="button"
                  accessibilityLabel={action.label}
                  onPress={() => selectAction(action)}
                  style={[styles.libraryActionPreset, selected && styles.libraryActionPresetSelected]}
                >
                  <Image source={{ uri: action.icon }} style={styles.libraryActionPresetImage} resizeMode="contain" />
                  <Text style={styles.libraryActionPresetText}>{action.label}</Text>
                </Pressable>
              )
            })}
          </View>
          <View style={styles.libraryActionFiles}>
            <LibraryImagePicker
              label={copy.resources.actionIconLabel}
              hint={copy.resources.chooseActionIcon}
              value={form.actionIconUrl}
              preview={form.actionIconUrl || actionOptions[0]?.icon}
              onPress={() => selectImage('actionIconUrl')}
              copy={copy}
            />
            <LibraryImagePicker
              label={copy.resources.actionImageLabel}
              hint={copy.resources.chooseActionImage}
              value={form.actionImageUrl}
              preview={form.actionImageUrl || actionOptions[0]?.image}
              onPress={() => selectImage('actionImageUrl')}
              copy={copy}
            />
          </View>
          <View style={styles.formGrid}>
            <FormField label={fields.action} value={form.actionType} onChangeText={(value) => updateField('actionType', value)} />
            <FormField label={fields.idealConditions} value={form.idealConditions} onChangeText={(value) => updateField('idealConditions', value)} multiline />
            <FormField label={fields.notes} value={form.usageNotes} onChangeText={(value) => updateField('usageNotes', value)} multiline />
          </View>
          <View style={styles.libraryLevelGrid}>
            <LibraryLevelSelect
              label={fields.difficulty}
              value={form.difficulty}
              options={getLureLevelOptions('difficulty', copy)}
              onChange={(value) => updateField('difficulty', value)}
              copy={copy}
            />
            <LibraryLevelSelect
              label={fields.effectiveness}
              value={form.effectiveness}
              options={getLureLevelOptions('effectiveness', copy)}
              onChange={(value) => updateField('effectiveness', value)}
              copy={copy}
            />
          </View>
        </View>
      )}

      {feedback && (
        <View style={styles.formFeedback}>
          <Text style={styles.formFeedbackText}>{feedback.text}</Text>
        </View>
      )}

      <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.saveEntry} disabled={saving} onPress={submit} style={[styles.submitButton, saving && styles.submitButtonDisabled]}>
        <Text style={styles.submitButtonText}>{saving ? copy.resources.saving : copy.resources.saveEntry}</Text>
      </Pressable>
    </View>
  )
}

function LibraryImagePicker({ label, hint, value, preview, onPress, copy, standalone = false, style }) {
  if (standalone) {
    return (
      <View style={styles.libraryImagePickerStandalone}>
        <Text style={styles.libraryImagePickerLabel}>{label}</Text>
        <Text style={styles.libraryImagePickerHint}>{value ? copy.resources.imageSelected : hint}</Text>
        <Pressable accessibilityRole="button" accessibilityLabel={label} onPress={onPress} style={styles.libraryImagePickerButton}>
          <Text style={styles.libraryImagePickerButtonText}>{copy.resources.chooseImage}</Text>
        </Pressable>
      </View>
    )
  }

  return (
    <View style={[styles.libraryImagePicker, style]}>
      <View style={styles.libraryImagePickerPreview}>
        <Image source={{ uri: preview }} style={styles.libraryImagePickerImage} resizeMode="contain" />
      </View>
      <View style={styles.libraryImagePickerCopy}>
        <Text style={styles.libraryImagePickerLabel}>{label}</Text>
        <Text style={styles.libraryImagePickerHint}>{value ? copy.resources.imageSelected : hint}</Text>
        <Pressable accessibilityRole="button" accessibilityLabel={label} onPress={onPress} style={styles.libraryImagePickerButton}>
          <Text style={styles.libraryImagePickerButtonText}>{copy.resources.chooseImage}</Text>
        </Pressable>
      </View>
    </View>
  )
}

function LibraryLevelSelect({ label, value, options, onChange, copy }) {
  const [open, setOpen] = useState(false)
  const selected = options.find((option) => option.value.toLowerCase() === String(value || '').trim().toLowerCase())

  return (
    <View style={styles.libraryLevelSelect}>
      <Text style={styles.formLabel}>{label}</Text>
      <Pressable
        accessibilityRole="combobox"
        accessibilityLabel={label}
        accessibilityState={{ expanded: open, selected: Boolean(selected) }}
        onPress={() => setOpen((current) => !current)}
        style={[styles.libraryLevelButton, open && styles.libraryLevelButtonOpen]}
      >
        <Text style={[styles.libraryLevelButtonText, !selected && styles.libraryLevelButtonPlaceholder]}>
          {selected?.label || copy.resources.chooseLevel}
        </Text>
        <Text style={styles.libraryLevelButtonIcon}>{open ? '-' : '+'}</Text>
      </Pressable>
      {selected && (
        <View style={styles.libraryLevelPreviewTrack}>
          <View style={[styles.libraryLevelPreviewFill, { width: `${selected.score * 100}%`, backgroundColor: selected.color }]} />
        </View>
      )}
      {open && (
        <View style={styles.libraryLevelDropdown}>
          {options.map((option) => {
            const isSelected = option.value === selected?.value

            return (
              <Pressable
                key={option.value}
                accessibilityRole="option"
                accessibilityLabel={option.label}
                accessibilityState={{ selected: isSelected }}
                onPress={() => {
                  onChange(option.value)
                  setOpen(false)
                }}
                style={[styles.libraryLevelOption, isSelected && styles.libraryLevelOptionSelected]}
              >
                <View style={[styles.libraryLevelOptionDot, { backgroundColor: option.color }]} />
                <Text style={[styles.libraryLevelOptionText, isSelected && styles.libraryLevelOptionTextSelected]}>{option.label}</Text>
              </Pressable>
            )
          })}
        </View>
      )}
    </View>
  )
}

function FishEnvironmentField({ value, onChange, copy }) {
  const options = [
    { value: 'FRESHWATER', label: copy.resources.freshwater },
    { value: 'SALTWATER', label: copy.resources.saltwater },
  ]

  return (
    <View style={styles.fishEnvironmentField}>
      <Text style={styles.formLabel}>{copy.resources.waterEnvironment}</Text>
      <Text style={styles.fishEnvironmentFieldHint}>{copy.resources.chooseWaterEnvironment}</Text>
      <View style={styles.fishEnvironmentFieldOptions}>
        {options.map((option) => {
          const selected = value === option.value
          const saltwater = option.value === 'SALTWATER'

          return (
            <Pressable
              key={option.value}
              accessibilityRole="radio"
              accessibilityLabel={option.label}
              accessibilityState={{ selected }}
              onPress={() => onChange(option.value)}
              style={[
                styles.fishEnvironmentFieldOption,
                selected && styles.fishEnvironmentFieldOptionSelected,
                saltwater && selected && styles.fishEnvironmentFieldOptionSaltwater,
              ]}
            >
              <View style={[styles.fishEnvironmentFieldDot, saltwater && styles.fishEnvironmentFieldDotSaltwater]} />
              <Text style={[styles.fishEnvironmentFieldOptionText, selected && styles.fishEnvironmentFieldOptionTextSelected]}>
                {option.label}
              </Text>
            </Pressable>
          )
        })}
      </View>
    </View>
  )
}

function MultiSelectCombo({ label, values, options, onChange, hint, emptyText, copy }) {
  const [open, setOpen] = useState(false)
  const selectedValues = Array.isArray(values) ? values : parseListValue(values)
  const selectedOptions = selectedValues.map((value) => options.find((option) => option.value === value) || { value, label: value })

  function toggleOption(option) {
    const nextValues = selectedValues.includes(option.value)
      ? selectedValues.filter((value) => value !== option.value)
      : [...selectedValues, option.value]

    onChange(nextValues)
  }

  function removeOption(value) {
    onChange(selectedValues.filter((selectedValue) => selectedValue !== value))
  }

  return (
    <View style={styles.libraryMultiSelect}>
      <Text style={styles.formLabel}>{label}</Text>
      <View style={styles.libraryMultiSelectBox}>
        {selectedOptions.length > 0 ? (
          <View style={styles.librarySelectionChips}>
            {selectedOptions.map((option) => (
              <Pressable
                key={option.value}
                accessibilityRole="button"
                accessibilityLabel={`${copy.resources.removeSelection}: ${option.label}`}
                onPress={() => removeOption(option.value)}
                style={styles.librarySelectionChip}
              >
                <Text style={styles.librarySelectionChipText}>{option.label}</Text>
                <Text style={styles.librarySelectionChipRemove}>x</Text>
              </Pressable>
            ))}
          </View>
        ) : (
          <Text style={styles.libraryMultiSelectPlaceholder}>{copy.resources.chooseOptions}</Text>
        )}
        <Pressable
          accessibilityRole="combobox"
          accessibilityLabel={label}
          accessibilityState={{ expanded: open, selected: selectedOptions.length > 0 }}
          onPress={() => setOpen((current) => !current)}
          style={[styles.libraryMultiSelectToggle, open && styles.libraryMultiSelectToggleOpen]}
        >
          <Text style={styles.libraryMultiSelectToggleText}>{open ? '-' : '+'}</Text>
        </Pressable>
      </View>
      <Text style={styles.libraryMultiSelectHint}>{hint || copy.resources.selectMultiple}</Text>
      {open && (
        <View style={styles.libraryMultiSelectDropdown}>
          {options.length > 0 ? (
            options.map((option) => {
              const selected = selectedValues.includes(option.value)

              return (
                <Pressable
                  key={option.value}
                  accessibilityRole="option"
                  accessibilityLabel={option.label}
                  accessibilityState={{ selected }}
                  onPress={() => toggleOption(option)}
                  style={[styles.libraryMultiSelectOption, selected && styles.libraryMultiSelectOptionSelected]}
                >
                  {option.image && <Image source={{ uri: option.image }} style={styles.libraryMultiSelectOptionImage} resizeMode="contain" />}
                  <Text style={[styles.libraryMultiSelectOptionText, selected && styles.libraryMultiSelectOptionTextSelected]}>{option.label}</Text>
                  <Text style={styles.libraryMultiSelectOptionMark}>{selected ? 'x' : '+'}</Text>
                </Pressable>
              )
            })
          ) : (
            <Text style={styles.libraryMultiSelectEmpty}>{emptyText || copy.resources.noOptionsAvailable}</Text>
          )}
        </View>
      )}
    </View>
  )
}

function readImageFromDevice() {
  if (typeof document === 'undefined') {
    return Promise.resolve(null)
  }

  return new Promise((resolve) => {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = 'image/*'
    input.onchange = () => {
      const file = input.files?.[0]

      if (!file) {
        resolve(null)
        return
      }

      const reader = new FileReader()
      reader.onload = () => resolve(typeof reader.result === 'string' ? reader.result : null)
      reader.onerror = () => resolve(null)
      reader.readAsDataURL(file)
    }
    input.click()
  })
}

function ResourceScreen({
  section,
  groups,
  loading,
  error,
  detail,
  onOpenDetail,
  onCloseDetail,
  onCreated,
  search,
  onSearchChange,
  onPageChange,
  compact,
  waterEnvironment,
  onWaterEnvironmentChange,
  copy,
}) {
  const [showCreateSpot, setShowCreateSpot] = useState(false)
  const [showCreatePlan, setShowCreatePlan] = useState(false)
  const canCreateSpot = section.id === 'spots'
  const canCreatePlan = section.id === 'plans'

  if (section.id === 'gallery') {
    return (
      <GalleryShowcase
        groups={groups}
        loading={loading}
        error={error}
        detail={detail}
        onOpenDetail={onOpenDetail}
        onCloseDetail={onCloseDetail}
        onSearchChange={onSearchChange}
        onPageChange={onPageChange}
        compact={compact}
        search={search}
        onCreated={onCreated}
        copy={copy}
      />
    )
  }

  if (section.id === 'library') {
    return (
      <LibraryShowcase
        groups={groups}
        loading={loading}
        error={error}
        detail={detail}
        onOpenDetail={onOpenDetail}
        onCloseDetail={onCloseDetail}
        onPageChange={onPageChange}
        compact={compact}
        waterEnvironment={waterEnvironment}
        onWaterEnvironmentChange={onWaterEnvironmentChange}
        onCreated={onCreated}
        copy={copy}
      />
    )
  }

  return (
    <View
      style={[
        styles.resourceStack,
        section.id === 'spots' && styles.resourceStackAtlas,
        section.id === 'plans' && styles.resourceStackMission,
        section.id === 'session' && styles.resourceStackLive,
        section.id === 'lureBox' && styles.resourceStackGear,
      ]}
    >
      {['gallery', 'library'].includes(section.id) ? (
        <SectionFeature section={section} groups={groups} compact={compact} copy={copy} />
      ) : (
        <WorkspaceHeader section={section} groups={groups} compact={compact} copy={copy} />
      )}

      <View
        style={[
          styles.resourceToolbar,
          section.id === 'spots' && styles.resourceToolbarAtlas,
          section.id === 'plans' && styles.resourceToolbarMission,
          section.id === 'session' && styles.resourceToolbarLive,
          section.id === 'lureBox' && styles.resourceToolbarGear,
          compact && styles.resourceToolbarCompact,
        ]}
      >
        <TextInput
          accessibilityLabel={copy.resources.searchPlaceholder}
          value={search}
          onChangeText={onSearchChange}
          placeholder={copy.resources.searchPlaceholder}
          placeholderTextColor="#6d7b75"
          style={styles.searchInput}
        />
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={copy.resources.clear}
          onPress={() => onSearchChange('')}
          style={styles.secondaryButton}
        >
          <Text style={styles.secondaryButtonText}>{copy.resources.clear}</Text>
        </Pressable>
        {canCreateSpot && (
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.resources.createSpot}
            onPress={() => {
              setShowCreatePlan(false)
              setShowCreateSpot((current) => !current)
            }}
            style={styles.primaryButton}
          >
            <Text style={styles.primaryButtonText}>
              {showCreateSpot ? copy.resources.cancel : copy.resources.createSpot}
            </Text>
          </Pressable>
        )}
        {canCreatePlan && (
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.resources.createPlan}
            onPress={() => {
              setShowCreateSpot(false)
              setShowCreatePlan((current) => !current)
            }}
            style={styles.primaryButton}
          >
            <Text style={styles.primaryButtonText}>
              {showCreatePlan ? copy.resources.cancel : copy.resources.createPlan}
            </Text>
          </Pressable>
        )}
      </View>

      {canCreateSpot && showCreateSpot && (
        <CreateSpotForm
          copy={copy}
          onCreated={onCreated}
        />
      )}

      {canCreatePlan && showCreatePlan && (
        <CreatePlanForm
          copy={copy}
          onCreated={onCreated}
        />
      )}

      {loading && (
        <View style={styles.loadingLine}>
          <ActivityIndicator color="#11c5b7" />
          <Text style={styles.loadingText}>{copy.loading}</Text>
        </View>
      )}

      {error && (
        <View style={styles.notice}>
          <Text style={styles.noticeText}>{copy.resources.loadError}</Text>
        </View>
      )}

      {detail?.item && <DetailPanel detail={detail} onClose={onCloseDetail} copy={copy} compact={compact} />}

      {groups.map((group) => (
        <View
          key={group.key}
          style={[
            styles.resourceGroup,
            section.id === 'spots' && styles.resourceGroupAtlas,
            section.id === 'plans' && styles.resourceGroupMission,
            section.id === 'session' && styles.resourceGroupLive,
            section.id === 'lureBox' && styles.resourceGroupGear,
          ]}
        >
          <View
            style={[
              styles.resourceGroupHeader,
              section.id === 'spots' && styles.resourceGroupHeaderAtlas,
              section.id === 'plans' && styles.resourceGroupHeaderMission,
              section.id === 'session' && styles.resourceGroupHeaderLive,
              section.id === 'lureBox' && styles.resourceGroupHeaderGear,
            ]}
          >
            <View style={styles.resourceGroupHeading}>
              <View style={[styles.groupMarker, { backgroundColor: groupTones[group.key]?.accent || '#11c5b7' }]} />
              <View>
                <Text style={styles.panelLabel}>{copy.resources.groups[group.key]}</Text>
              <Text style={styles.resourceCount}>
                {group.totalItems} {copy.resources.total}
              </Text>
              </View>
            </View>

            <PaginationControls group={group} onPageChange={onPageChange} copy={copy} />
          </View>

          {group.items.length > 0 ? (
            <View style={styles.resourceGrid}>
              {group.items.map((item) => (
                <ResourceCard
                  key={`${group.key}-${getItemId(item, group.key)}`}
                  item={item}
                  group={group}
                  copy={copy}
                  onPress={onOpenDetail}
                />
              ))}
            </View>
          ) : (
            <View style={styles.emptyPanel}>
              <Text style={styles.emptyText}>{copy.resources.empty}</Text>
            </View>
          )}
        </View>
      ))}
    </View>
  )
}

function CreateSpotForm({ copy, onCreated }) {
  const [form, setForm] = useState({
    name: '',
    description: '',
    latitude: '',
    longitude: '',
    waterType: '',
    favoriteSpecies: '',
  })
  const [saving, setSaving] = useState(false)
  const [feedback, setFeedback] = useState(null)
  const fields = copy.resources.fields

  function updateField(field, value) {
    setFeedback(null)
    setForm((current) => ({
      ...current,
      [field]: value,
    }))
  }

  async function submit() {
    const latitude = Number(form.latitude)
    const longitude = Number(form.longitude)

    if (!form.name.trim() || !Number.isFinite(latitude) || !Number.isFinite(longitude) || !form.waterType.trim()) {
      setFeedback({ type: 'error', text: copy.resources.requiredFields })
      return
    }

    setSaving(true)
    setFeedback(null)

    try {
      const response = await fetch('/api/spots', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: form.name.trim(),
          description: toNullableText(form.description),
          latitude,
          longitude,
          waterType: form.waterType.trim(),
          favoriteSpecies: toNullableText(form.favoriteSpecies),
        }),
      })

      if (!response.ok) {
        throw new Error('Create spot failed')
      }

      setForm({
        name: '',
        description: '',
        latitude: '',
        longitude: '',
        waterType: '',
        favoriteSpecies: '',
      })
      setFeedback({ type: 'success', text: copy.resources.createSpotSuccess })
      onCreated()
    } catch {
      setFeedback({ type: 'error', text: copy.resources.createSpotError })
    } finally {
      setSaving(false)
    }
  }

  return (
    <View style={styles.formPanel}>
      <View>
        <Text style={styles.panelLabel}>{copy.resources.createSpot}</Text>
        <Text style={styles.formTitle}>{copy.sections.spots.title}</Text>
      </View>

      <View style={styles.formGrid}>
        <FormField
          label={fields.name}
          value={form.name}
          onChangeText={(value) => updateField('name', value)}
        />
        <FormField
          label={fields.waterType}
          value={form.waterType}
          onChangeText={(value) => updateField('waterType', value)}
        />
        <FormField
          label={fields.latitude}
          value={form.latitude}
          onChangeText={(value) => updateField('latitude', value)}
          keyboardType="decimal-pad"
        />
        <FormField
          label={fields.longitude}
          value={form.longitude}
          onChangeText={(value) => updateField('longitude', value)}
          keyboardType="decimal-pad"
        />
        <FormField
          label={fields.favoriteSpecies}
          value={form.favoriteSpecies}
          onChangeText={(value) => updateField('favoriteSpecies', value)}
        />
        <FormField
          label={fields.description}
          value={form.description}
          onChangeText={(value) => updateField('description', value)}
          multiline
        />
      </View>

      {feedback && (
        <View style={[styles.formFeedback, feedback.type === 'success' && styles.formFeedbackSuccess]}>
          <Text style={[styles.formFeedbackText, feedback.type === 'success' && styles.formFeedbackTextSuccess]}>
            {feedback.text}
          </Text>
        </View>
      )}

      <Pressable
        accessibilityRole="button"
        accessibilityLabel={copy.resources.saveSpot}
        disabled={saving}
        onPress={submit}
        style={[styles.submitButton, saving && styles.submitButtonDisabled]}
      >
        <Text style={styles.submitButtonText}>{saving ? copy.resources.saving : copy.resources.saveSpot}</Text>
      </Pressable>
    </View>
  )
}

function CreatePlanForm({ copy, onCreated }) {
  const [form, setForm] = useState({
    spotId: null,
    plannedDate: '',
    plannedTime: '',
    targetSpecies: '',
    waterClarity: 'CLEAR',
    waterLevel: 'NORMAL',
    notes: '',
  })
  const [spots, setSpots] = useState([])
  const [spotsLoading, setSpotsLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [feedback, setFeedback] = useState(null)
  const fields = copy.resources.fields
  const clarityOptions = [
    { value: 'CLEAR', label: copy.resources.waterClarityOptions.clear },
    { value: 'STAINED', label: copy.resources.waterClarityOptions.stained },
    { value: 'MUDDY', label: copy.resources.waterClarityOptions.muddy },
  ]
  const levelOptions = [
    { value: 'LOW', label: copy.resources.waterLevelOptions.low },
    { value: 'NORMAL', label: copy.resources.waterLevelOptions.normal },
    { value: 'HIGH', label: copy.resources.waterLevelOptions.high },
  ]

  useEffect(() => {
    let ignore = false

    async function loadSpots() {
      setSpotsLoading(true)

      try {
        const response = await fetch('/api/spots?page=0&size=50&sortBy=name&sortDirection=asc')

        if (!response.ok) {
          throw new Error('Spots unavailable')
        }

        const payload = await response.json()
        const items = payload.items || []

        if (!ignore) {
          setSpots(items)
          setForm((current) => ({
            ...current,
            spotId: current.spotId || items[0]?.id || null,
          }))
        }
      } catch {
        if (!ignore) {
          setSpots([])
        }
      } finally {
        if (!ignore) {
          setSpotsLoading(false)
        }
      }
    }

    loadSpots()

    return () => {
      ignore = true
    }
  }, [])

  function updateField(field, value) {
    setFeedback(null)
    setForm((current) => ({
      ...current,
      [field]: value,
    }))
  }

  async function submit() {
    if (
      !form.spotId ||
      !isIsoDate(form.plannedDate) ||
      !form.targetSpecies.trim() ||
      !form.waterClarity ||
      !form.waterLevel
    ) {
      setFeedback({ type: 'error', text: copy.resources.planRequiredFields })
      return
    }

    setSaving(true)
    setFeedback(null)

    try {
      const response = await fetch('/api/plans', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          spotId: form.spotId,
          plannedDate: form.plannedDate.trim(),
          plannedTime: toNullableTime(form.plannedTime),
          targetSpecies: form.targetSpecies.trim(),
          waterClarity: form.waterClarity,
          waterLevel: form.waterLevel,
          notes: toNullableText(form.notes),
        }),
      })

      if (!response.ok) {
        throw new Error('Create plan failed')
      }

      setForm((current) => ({
        ...current,
        plannedDate: '',
        plannedTime: '',
        targetSpecies: '',
        notes: '',
      }))
      setFeedback({ type: 'success', text: copy.resources.createPlanSuccess })
      onCreated()
    } catch {
      setFeedback({ type: 'error', text: copy.resources.createPlanError })
    } finally {
      setSaving(false)
    }
  }

  return (
    <View style={styles.formPanel}>
      <View>
        <Text style={styles.panelLabel}>{copy.resources.createPlan}</Text>
        <Text style={styles.formTitle}>{copy.sections.plans.title}</Text>
      </View>

      <View style={styles.formSection}>
        <Text style={styles.formLabel}>{copy.resources.chooseSpot}</Text>
        {spotsLoading && (
          <View style={styles.loadingLine}>
            <ActivityIndicator color="#11c5b7" />
            <Text style={styles.loadingText}>{copy.loading}</Text>
          </View>
        )}
        {!spotsLoading && spots.length === 0 && (
          <View style={styles.formFeedback}>
            <Text style={styles.formFeedbackText}>{copy.resources.noSpotsForPlan}</Text>
          </View>
        )}
        {!spotsLoading && spots.length > 0 && (
          <View style={styles.choiceGrid}>
            {spots.map((spot) => (
              <Pressable
                key={spot.id}
                accessibilityRole="button"
                accessibilityLabel={spot.name}
                onPress={() => updateField('spotId', spot.id)}
                style={[styles.choiceChip, form.spotId === spot.id && styles.choiceChipSelected]}
              >
                <Text style={[styles.choiceChipText, form.spotId === spot.id && styles.choiceChipTextSelected]}>
                  {spot.name}
                </Text>
                <Text style={[styles.choiceChipMeta, form.spotId === spot.id && styles.choiceChipMetaSelected]}>
                  {compactLine(spot.waterType, spot.favoriteSpecies)}
                </Text>
              </Pressable>
            ))}
          </View>
        )}
      </View>

      <View style={styles.formGrid}>
        <FormField
          label={fields.plannedFor}
          value={form.plannedDate}
          onChangeText={(value) => updateField('plannedDate', value)}
          placeholder={copy.resources.plannedDateHint}
        />
        <FormField
          label={copy.resources.plannedTimeHint}
          value={form.plannedTime}
          onChangeText={(value) => updateField('plannedTime', value)}
          placeholder={copy.resources.plannedTimeHint}
        />
        <FormField
          label={fields.targetSpecies}
          value={form.targetSpecies}
          onChangeText={(value) => updateField('targetSpecies', value)}
        />
      </View>

      <ChoiceGroup
        label={fields.waterClarity}
        value={form.waterClarity}
        options={clarityOptions}
        onChange={(value) => updateField('waterClarity', value)}
      />
      <ChoiceGroup
        label={fields.waterLevel}
        value={form.waterLevel}
        options={levelOptions}
        onChange={(value) => updateField('waterLevel', value)}
      />

      <FormField
        label={fields.notes}
        value={form.notes}
        onChangeText={(value) => updateField('notes', value)}
        multiline
      />

      {feedback && (
        <View style={[styles.formFeedback, feedback.type === 'success' && styles.formFeedbackSuccess]}>
          <Text style={[styles.formFeedbackText, feedback.type === 'success' && styles.formFeedbackTextSuccess]}>
            {feedback.text}
          </Text>
        </View>
      )}

      <Pressable
        accessibilityRole="button"
        accessibilityLabel={copy.resources.savePlan}
        disabled={saving || spots.length === 0}
        onPress={submit}
        style={[styles.submitButton, (saving || spots.length === 0) && styles.submitButtonDisabled]}
      >
        <Text style={styles.submitButtonText}>{saving ? copy.resources.saving : copy.resources.savePlan}</Text>
      </Pressable>
    </View>
  )
}

function ChoiceGroup({ label, value, options, onChange }) {
  return (
    <View style={styles.formSection}>
      <Text style={styles.formLabel}>{label}</Text>
      <View style={styles.choiceGrid}>
        {options.map((option) => {
          const selected = value === option.value

          return (
            <Pressable
              key={option.value}
              accessibilityRole="button"
              accessibilityLabel={option.label}
              onPress={() => onChange(option.value)}
              style={[styles.choiceChip, selected && styles.choiceChipSelected]}
            >
              <Text style={[styles.choiceChipText, selected && styles.choiceChipTextSelected]}>{option.label}</Text>
            </Pressable>
          )
        })}
      </View>
    </View>
  )
}

function FormField({ label, value, onChangeText, keyboardType, placeholder, multiline = false, fieldStyle }) {
  return (
    <View style={[styles.formField, fieldStyle, multiline && styles.formFieldWide]}>
      <Text style={styles.formLabel}>{label}</Text>
      <TextInput
        accessibilityLabel={label}
        value={value}
        onChangeText={onChangeText}
        keyboardType={keyboardType}
        multiline={multiline}
        placeholder={placeholder || label}
        placeholderTextColor="#7b8982"
        style={[styles.formInput, multiline && styles.formInputMultiline]}
      />
    </View>
  )
}

function DetailPanel({ detail, onClose, onEditItem, onDeleteItem, copy, compact }) {
  const display = getItemDisplay(detail.item, detail.group.key, copy)
  const image = getDetailImage(detail.group, detail.item, detail.data, display)
  const tone = groupTones[detail.group.key] || groupTones.spots

  return (
    <View style={[styles.detailPanel, { borderColor: tone.borderColor }]}>
      {!['catches', 'fish'].includes(detail.group.key) && (
        <View
          style={[styles.detailTopbar, { backgroundColor: tone.backgroundColor, borderColor: tone.borderColor }]}
          testID="detail-topbar"
        >
          <View style={[styles.detailTopbarMarker, { backgroundColor: tone.accent }]} />
          <Image source={{ uri: image }} style={styles.detailImage} resizeMode="cover" />
          <View style={styles.detailTitleBlock}>
            <Text style={[styles.panelLabel, { color: tone.accent }]}>{copy.resources.details}</Text>
            <Text style={styles.detailTitle}>{display.title}</Text>
          <Text style={styles.detailSubtitle}>{display.meta}</Text>
          </View>
          {onEditItem && (
            <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.editEntry} onPress={onEditItem} style={styles.detailActionButton}>
              <Text style={styles.detailActionButtonText}>{copy.resources.editEntry}</Text>
            </Pressable>
          )}
          {onDeleteItem && (
            <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.deleteEntry} onPress={onDeleteItem} style={styles.detailDeleteButton}>
              <Text style={styles.detailDeleteButtonText}>{copy.resources.deleteEntry}</Text>
            </Pressable>
          )}
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.resources.close}
            onPress={onClose}
            style={styles.closeButton}
          >
            <Text style={styles.closeButtonText}>{copy.resources.close}</Text>
          </Pressable>
        </View>
      )}

      {detail.loading && (
        <View style={styles.loadingLine}>
          <ActivityIndicator color={tone.accent} />
          <Text style={styles.loadingText}>{copy.resources.loadingDetails}</Text>
        </View>
      )}

      {detail.error && (
        <View style={styles.notice}>
          <Text style={styles.noticeText}>{copy.resources.detailLoadError}</Text>
        </View>
      )}

      {detail.group.key === 'catches' && (
        <CatchDetail
          detail={detail}
          image={image}
          copy={copy}
          tone={tone}
          compact={compact}
          onClose={onClose}
          onEdit={onEditItem}
          onDelete={onDeleteItem}
        />
      )}
      {detail.group.key === 'spots' && <SpotDetail detail={detail} image={image} copy={copy} tone={tone} />}
      {detail.group.key === 'plans' && <PlanDetail detail={detail} copy={copy} tone={tone} />}
      {detail.group.key === 'sessions' && <SessionDetail detail={detail} copy={copy} tone={tone} />}
      {detail.group.key === 'lureBox' && <LureBoxDetail detail={detail} image={image} copy={copy} tone={tone} />}
      {detail.group.key === 'fish' && (
        <FishDetail
          detail={detail}
          image={image}
          copy={copy}
          tone={tone}
          compact={compact}
          onClose={onClose}
          onEdit={onEditItem}
          onDelete={onDeleteItem}
        />
      )}
      {detail.group.key === 'lureLibrary' && (
        <LureLibraryDetail detail={detail} image={image} copy={copy} tone={tone} compact={compact} />
      )}
      {!['catches', 'spots', 'plans', 'sessions', 'lureBox', 'fish', 'lureLibrary'].includes(detail.group.key) && (
        <GenericDetail detail={detail} copy={copy} />
      )}
    </View>
  )
}

function CatchDetail({ detail, image, copy, tone, compact, onClose, onEdit, onDelete }) {
  const item = detail.item
  const data = detail.data || item
  const fields = copy.resources.fields
  const sessionDate = formatSchedule(data.date || item.sessionDate, data.startTime || item.sessionStartTime, copy)

  return (
    <View style={[styles.catchDetailView, compact && styles.catchDetailViewCompact]}>
      <View style={[styles.catchHeroFrame, compact && styles.catchHeroFrameCompact, { backgroundColor: tone.imageBackground }]}>
        <Image source={{ uri: image }} style={styles.catchHeroImage} resizeMode="contain" />
        <View style={styles.catchHeroShade} />
        <View style={[styles.catchHeroTag, { backgroundColor: tone.accent }]}>
          <Text style={styles.catchHeroTagText}>{fields.species}</Text>
        </View>
        <View style={styles.catchHeroActions}>
          {onEdit && (
            <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.editEntry} onPress={onEdit} style={styles.catchHeroActionButton}>
              <Text style={styles.catchHeroActionText}>{copy.resources.editEntry}</Text>
            </Pressable>
          )}
          {onDelete && (
            <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.deleteEntry} onPress={onDelete} style={styles.catchHeroDeleteButton}>
              <Text style={styles.catchHeroDeleteText}>{copy.resources.deleteEntry}</Text>
            </Pressable>
          )}
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.resources.close}
            onPress={onClose}
            style={styles.catchHeroClose}
          >
            <Text style={styles.catchHeroCloseText}>{copy.resources.close}</Text>
          </Pressable>
        </View>
        <View style={styles.catchHeroCopy}>
          <Text style={styles.catchHeroEyebrow}>{copy.dashboard.latestCatch}</Text>
          <Text style={styles.catchHeroTitle}>{item.species || copy.dashboard.speciesFallback}</Text>
          <Text style={styles.catchHeroMeta}>{item.spotName || data.spotName || copy.dashboard.unnamedSpot}</Text>
        </View>
      </View>

      <View style={[styles.catchStatStrip, { borderColor: tone.borderColor }]}>
        <CatchStatBlock label={fields.size} value={formatCatchSize(item) || '-'} tone={tone.accent} />
        {formatCatchWeight(item) && (
          <CatchStatBlock label={fields.weight} value={formatCatchWeight(item)} tone={tone.accent} />
        )}
      </View>

      <View style={styles.catchContextPanel}>
        <View style={styles.catchContextHeader}>
          <View style={[styles.catchContextHeaderBar, { backgroundColor: tone.accent }]} />
          <View>
            <Text style={[styles.detailSectionEyebrow, { color: tone.accent }]}>{fields.session}</Text>
            <Text style={styles.catchContextTitle}>{sessionDate}</Text>
          </View>
        </View>
        <View style={styles.catchContextGrid}>
          <CatchContextItem label={fields.spot} value={item.spotName || data.spotName} />
        </View>
        {item.lureName && (
          <CatchLureCard item={item} copy={copy} />
        )}
      </View>
    </View>
  )
}

function CatchLureCard({ item, copy }) {
  const lureImage = getImageSource(item.lureImageUrl, getLureImage(item.lureName))

  return (
    <View style={styles.catchLureCard}>
      <Image source={{ uri: lureImage }} style={styles.catchLureImage} resizeMode="contain" />
      <View style={styles.catchLureCopy}>
        <Text style={styles.catchLureLabel}>{copy.resources.lureUsed}</Text>
        <Text style={styles.catchLureName}>{item.lureName}</Text>
      </View>
    </View>
  )
}

function CatchStatBlock({ label, value, tone }) {
  return (
    <View style={styles.catchStatBlock}>
      <View style={[styles.catchStatDot, { backgroundColor: tone }]} />
      <Text style={styles.catchStatLabel}>{label}</Text>
      <Text style={styles.catchStatValue}>{String(value)}</Text>
    </View>
  )
}

function CatchContextItem({ label, value, wide }) {
  if (!hasDetailValue(value)) {
    return null
  }

  return (
    <View style={[styles.catchContextItem, wide && styles.catchContextItemWide]}>
      <Text style={styles.catchContextLabel}>{label}</Text>
      <Text style={styles.catchContextValue}>{String(value)}</Text>
    </View>
  )
}

function SpotDetail({ detail, image, copy, tone }) {
  const source = detail.data || detail.item
  const fields = copy.resources.fields

  return (
    <View style={styles.detailContent}>
      <View style={[styles.spotMapPanel, { backgroundColor: tone.imageBackground }]}>
        <Image source={{ uri: image }} style={styles.spotDetailImage} resizeMode="cover" />
        <View style={styles.spotCoordinatesBadge}>
          <Text style={styles.spotCoordinatesLabel}>{fields.coordinates}</Text>
          <Text style={styles.spotCoordinatesValue}>{formatCoordinates(source.latitude, source.longitude) || '-'}</Text>
        </View>
      </View>
      <View style={styles.detailStatGrid}>
        <DetailStat label={fields.waterType} value={source.waterType || '-'} tone={tone.accent} />
        <DetailStat label={fields.favoriteSpecies} value={source.favoriteSpecies || '-'} tone={tone.accent} />
        <DetailStat label={fields.createdAt} value={formatDateTime(source.createdAt) || '-'} tone={tone.accent} />
      </View>
      <DetailSection title={fields.description} tone={tone.accent}>
        <Text style={styles.detailLongText}>{source.description || copy.resources.empty}</Text>
      </DetailSection>
    </View>
  )
}

function PlanDetail({ detail, copy, tone }) {
  const source = detail.data || detail.item
  const fields = copy.resources.fields

  return (
    <View style={styles.detailContent}>
      <View style={[styles.planMissionHeader, { backgroundColor: tone.imageBackground }]}>
        <View>
          <Text style={[styles.detailSectionEyebrow, { color: tone.accent }]}>{copy.sections.plans.title}</Text>
          <Text style={styles.detailFeatureTitle}>{source.targetSpecies || copy.dashboard.speciesFallback}</Text>
        </View>
        <View style={[styles.planDateBadge, { backgroundColor: tone.accent }]}>
          <Text style={styles.planDateText}>{formatSchedule(source.plannedDate, source.plannedTime, copy)}</Text>
        </View>
      </View>
      <View style={styles.planSteps}>
        <PlanStep number="01" label={fields.spot} value={source.spotName} tone={tone.accent} />
        <PlanStep number="02" label={fields.waterClarity} value={source.waterClarity} tone={tone.accent} />
        <PlanStep number="03" label={fields.waterLevel} value={source.waterLevel} tone={tone.accent} />
      </View>
      <DetailSection title={fields.notes} tone={tone.accent}>
        <Text style={styles.detailLongText}>{source.notes || copy.resources.empty}</Text>
      </DetailSection>
    </View>
  )
}

function SessionDetail({ detail, copy, tone }) {
  const source = detail.data || detail.item
  const fields = copy.resources.fields
  const isFinished = String(source.status || '').toLowerCase() === 'finished'
  const status = source.status || copy.resources.status

  return (
    <View style={styles.detailContent}>
      <View style={[styles.sessionStatusHeader, { backgroundColor: tone.imageBackground }]}>
        <View>
          <Text style={[styles.detailSectionEyebrow, { color: tone.accent }]}>{fields.status}</Text>
          <Text style={styles.detailFeatureTitle}>{status}</Text>
        </View>
        <View style={[styles.sessionStatusOrb, { backgroundColor: tone.accent }]}>
          <Text style={styles.sessionStatusOrbText}>{isFinished ? 'OK' : 'LIVE'}</Text>
        </View>
      </View>
      <View style={styles.sessionTimeline}>
        <TimelinePoint label={fields.session} value={formatSchedule(source.date, source.startTime, copy)} tone={tone.accent} />
        <View style={[styles.timelineLine, { backgroundColor: tone.accent }]} />
        <TimelinePoint label={fields.duration} value={formatDuration(source.durationMinutes) || '--'} tone={tone.accent} />
        <View style={[styles.timelineLine, { backgroundColor: tone.accent }]} />
        <TimelinePoint label={fields.result} value={formatBoolean(source.success, copy) || '--'} tone={tone.accent} />
      </View>
      <DetailSection title={fields.notes} tone={tone.accent}>
        <DetailLine label={fields.spot} value={source.spotName} />
        <DetailLine label={fields.targetSpecies} value={source.targetSpecies} />
        <DetailLine label={fields.finalNotes} value={source.finalNotes || source.notes} />
        <DetailLine label={fields.rating} value={formatRating(source.rating)} />
      </DetailSection>
    </View>
  )
}

function LureBoxDetail({ detail, image, copy, tone }) {
  const source = detail.data || detail.item
  const fields = copy.resources.fields

  return (
    <View style={styles.detailContent}>
      <View style={styles.lureInventoryHero}>
        <View style={[styles.lureInventoryImageFrame, { backgroundColor: tone.imageBackground }]}>
          <Image source={{ uri: image }} style={styles.lureInventoryImage} resizeMode="cover" />
        </View>
        <View style={styles.lureInventoryCopy}>
          <Text style={[styles.detailSectionEyebrow, { color: tone.accent }]}>{copy.menu.lureBox}</Text>
          <Text style={styles.detailFeatureTitle}>{source.name || copy.menu.lureBox}</Text>
          <Text style={styles.detailLongText}>{compactLine(source.type, source.color, source.size)}</Text>
        </View>
        <View style={[styles.quantityBlock, { backgroundColor: tone.accent }]}>
          <Text style={styles.quantityValue}>{source.quantity ?? 0}</Text>
          <Text style={styles.quantityLabel}>{fields.quantity}</Text>
        </View>
      </View>
      <View style={styles.detailStatGrid}>
        <DetailStat label={fields.condition} value={source.condition || '-'} tone={tone.accent} />
        <DetailStat label={fields.active} value={formatBoolean(source.active, copy) || '-'} tone={tone.accent} />
        <DetailStat label={fields.targetSpecies} value={source.targetSpecies || '-'} tone={tone.accent} />
        <DetailStat label={fields.waterType} value={source.waterType || '-'} tone={tone.accent} />
      </View>
      <DetailSection title={fields.personalNotes} tone={tone.accent}>
        <Text style={styles.detailLongText}>{source.personalNotes || source.notes || copy.resources.empty}</Text>
      </DetailSection>
    </View>
  )
}

function FishDetail({ detail, image, copy, tone, compact, onClose, onEdit, onDelete }) {
  const source = detail.data || detail.item
  const fields = copy.resources.fields
  const strikeZone = formatOptionList(source.strikeZone, getFishStrikeZoneOptions(copy))
  const commonZones = formatOptionList(source.commonZones, getFishCommonZoneOptions(copy))
  const favoriteLures = formatListText(source.favoriteLures)
  const environment = normalizeWaterEnvironment(source.waterEnvironment)

  return (
    <View style={[styles.fishDetailView, compact && styles.fishDetailViewCompact]}>
      <View style={styles.fishDetailTopline}>
        <View>
          <Text style={[styles.detailSectionEyebrow, { color: tone.accent }]}>{copy.resources.groups.fish}</Text>
          <Text style={styles.fishDetailToplineText}>{copy.resources.libraryFishHint}</Text>
        </View>
        <View style={styles.fishDetailActions}>
          {onEdit && (
            <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.editEntry} onPress={onEdit} style={styles.fishDetailActionButton}>
              <Text style={styles.fishDetailActionButtonText}>{copy.resources.editEntry}</Text>
            </Pressable>
          )}
          {onDelete && (
            <Pressable accessibilityRole="button" accessibilityLabel={copy.resources.deleteEntry} onPress={onDelete} style={styles.fishDetailDeleteButton}>
              <Text style={styles.fishDetailDeleteButtonText}>{copy.resources.deleteEntry}</Text>
            </Pressable>
          )}
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.resources.close}
            onPress={onClose}
            style={styles.fishDetailClose}
          >
            <Text style={styles.fishDetailCloseText}>{copy.resources.close}</Text>
          </Pressable>
        </View>
      </View>

      <View style={[styles.fishDetailHero, compact && styles.fishDetailHeroCompact, { backgroundColor: tone.imageBackground }]}>
        <View style={styles.fishDetailImageFrame}>
          <Image source={{ uri: image }} style={styles.fishDetailImage} resizeMode="contain" />
        </View>
        <View style={styles.fishDetailCopy}>
          <Text style={styles.fishDetailKicker}>{copy.resources.groups.fish}</Text>
          <Text style={styles.fishDetailTitle}>{source.name || copy.menu.library}</Text>
          <FishEnvironmentBadge environment={environment} copy={copy} large />
          <Text style={styles.fishDetailDescription}>{source.description || copy.resources.empty}</Text>
          <View style={styles.fishDetailTags}>
            <FishTag label={fields.strikeZone} value={strikeZone} tone={tone.accent} />
            <FishTag label={fields.activeTimes} value={source.activeTimes} tone={tone.accent} />
          </View>
        </View>
      </View>

      <View style={styles.fishFieldNotes}>
        <View style={styles.fishFieldNotesHeader}>
          <View style={[styles.fishFieldNotesBar, { backgroundColor: tone.accent }]} />
          <View>
            <Text style={styles.fishFieldNotesEyebrow}>{fields.habitat}</Text>
            <Text style={styles.fishFieldNotesTitle}>{copy.resources.libraryFishHint}</Text>
          </View>
        </View>
        <FishInsightRow label={fields.habitat} value={source.habitatNotes} tone={tone.accent} />
        <FishInsightRow label={fields.zones} value={commonZones} tone={tone.accent} />
        <FishInsightRow label={fields.favoriteLures} value={favoriteLures} tone={tone.accent} />
      </View>
    </View>
  )
}

function FishTag({ label, value, tone }) {
  if (!hasDetailValue(value)) {
    return null
  }

  return (
    <View style={styles.fishTag}>
      <View style={[styles.fishTagDot, { backgroundColor: tone }]} />
      <View style={styles.fishTagCopy}>
        <Text style={styles.fishTagLabel}>{label}</Text>
        <Text style={styles.fishTagValue}>{String(value)}</Text>
      </View>
    </View>
  )
}

function FishEnvironmentBadge({ environment, copy, large = false }) {
  const label = getWaterEnvironmentLabel(environment, copy)

  return (
    <View
      style={[
        styles.fishEnvironmentBadge,
        large && styles.fishEnvironmentBadgeLarge,
        environment === 'SALTWATER' ? styles.fishEnvironmentBadgeSaltwater : styles.fishEnvironmentBadgeFreshwater,
      ]}
    >
      <View style={[styles.fishEnvironmentBadgeDot, environment === 'SALTWATER' ? styles.fishEnvironmentBadgeDotSaltwater : styles.fishEnvironmentBadgeDotFreshwater]} />
      <Text style={styles.fishEnvironmentBadgeText}>{label}</Text>
    </View>
  )
}

function FishInsightRow({ label, value, tone }) {
  if (!hasDetailValue(value)) {
    return null
  }

  return (
    <View style={styles.fishInsightRow}>
      <View style={[styles.fishInsightDot, { backgroundColor: tone }]} />
      <Text style={styles.fishInsightLabel}>{label}</Text>
      <Text style={styles.fishInsightValue}>{String(value)}</Text>
    </View>
  )
}

function LureLibraryDetail({ detail, image, copy, tone, compact }) {
  const source = detail.data || detail.item
  const fields = copy.resources.fields
  const rankedActions = getLureActionOptions(source, copy)
  const storedAction = source.actionIconUrl || source.actionImageUrl
    ? {
        ...rankedActions[0],
        id: 'stored-action',
        label: source.actionType || rankedActions[0]?.label,
        icon: source.actionIconUrl || rankedActions[0]?.icon,
        image: source.actionImageUrl || rankedActions[0]?.image,
      }
    : null
  const actions = storedAction ? [storedAction] : rankedActions
  const [selectedActionId, setSelectedActionId] = useState(actions[0]?.id)
  const [showActionGuide, setShowActionGuide] = useState(false)
  const selectedAction = actions.find((action) => action.id === selectedActionId) || actions[0]

  const handleActionSelect = (action) => {
    setSelectedActionId(action.id)
    setShowActionGuide(true)
  }

  return (
    <View style={styles.lureDetailView}>
      <View style={styles.techniqueDetailGrid}>
        <View style={[styles.techniqueImageFrame, { backgroundColor: tone.imageBackground }]}>
          <Image source={{ uri: image }} style={styles.techniqueImage} resizeMode="contain" />
        </View>
        <View style={styles.techniqueCopy}>
          <Text style={[styles.detailSectionEyebrow, { color: tone.accent }]}>{copy.resources.groups.lureLibrary}</Text>
          <Text style={styles.lureDetailTitle}>{source.name || copy.menu.library}</Text>
          <Text style={styles.lureDetailDescription}>{source.description || copy.resources.empty}</Text>
          <View style={styles.lureIdentityFacts}>
            <LureInfoLine label={fields.type} value={source.type} />
            <LureInfoLine label={fields.idealConditions} value={source.idealConditions} />
          </View>
        </View>
      </View>

      {selectedAction && (
        <LureActionStudio
          actions={actions}
          selectedAction={selectedAction}
          showGuide={showActionGuide}
          onSelect={handleActionSelect}
          onToggleGuide={() => setShowActionGuide((current) => !current)}
          copy={copy}
          tone={tone}
          compact={compact}
        />
      )}

      <View style={styles.techniqueScoreRow}>
        <ScoreBar label={fields.difficulty} value={source.difficulty} kind="difficulty" copy={copy} />
        <ScoreBar label={fields.effectiveness} value={source.effectiveness} kind="effectiveness" copy={copy} />
      </View>

      <View style={styles.lureInfoRail}>
        <View style={styles.lureInfoRailHeader}>
          <View style={[styles.lureInfoRailBar, { backgroundColor: tone.accent }]} />
          <Text style={styles.lureInfoRailTitle}>{fields.action}</Text>
        </View>
        <LureInfoLine label={fields.action} value={source.actionType} />
        <LureInfoLine label={fields.idealConditions} value={source.idealConditions} />
        <LureInfoLine label={fields.notes} value={source.usageNotes} />
      </View>
    </View>
  )
}

function LureActionStudio({ actions, selectedAction, showGuide, onSelect, onToggleGuide, copy, tone, compact }) {
  return (
    <View style={styles.lureActionStudio}>
      <View style={styles.lureActionHeader}>
        <View>
          <Text style={[styles.detailSectionEyebrow, { color: tone.accent }]}>{copy.resources.actionGuideTitle}</Text>
          <Text style={styles.lureActionHint}>{copy.resources.actionGuideHint}</Text>
        </View>
        <Text style={styles.lureActionCount}>{actions.length} {copy.resources.actionOptions}</Text>
      </View>

      <View
        style={[styles.lureActionStage, compact && styles.lureActionStageCompact, showGuide && styles.lureActionStageGuide, { borderColor: tone.borderColor }]}
      >
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={`${selectedAction.label}: ${showGuide ? copy.resources.actionGuideBack : copy.resources.actionGuideOpen}`}
          onPress={onToggleGuide}
          style={[styles.lureActionStageVisual, showGuide && styles.lureActionStageVisualGuide, { backgroundColor: tone.imageBackground }]}
        >
          <Image
            source={{ uri: showGuide ? selectedAction.image : selectedAction.icon }}
            style={styles.lureActionStageImage}
            resizeMode="contain"
          />
        </Pressable>
        <View style={[styles.lureActionStageCopy, showGuide && styles.lureActionStageCopyGuide]}>
          <Text style={[styles.lureActionStageLabel, showGuide && styles.lureActionStageLabelGuide]}>{showGuide ? copy.resources.actionGuideVisual : fieldsLabel(copy, 'action')}</Text>
          <Text style={[styles.lureActionStageTitle, showGuide && styles.lureActionStageTitleGuide]}>{selectedAction.label}</Text>
          <Text style={[styles.lureActionStageDescription, showGuide && styles.lureActionStageDescriptionGuide]}>{selectedAction.description}</Text>
          <Text style={[styles.lureActionStagePrompt, { color: tone.accent }]}>
            {showGuide ? copy.resources.actionGuideBack : copy.resources.actionGuideOpen}
          </Text>
        </View>
      </View>

      <View style={styles.lureActionPicker}>
        {actions.map((action) => {
          const selected = action.id === selectedAction.id

          return (
            <Pressable
              key={action.id}
              accessibilityRole="button"
              accessibilityLabel={action.label}
              onPress={() => onSelect(action)}
              style={[styles.lureActionOption, selected && { borderColor: tone.accent, backgroundColor: tone.imageBackground }]}
            >
              <Image source={{ uri: action.icon }} style={styles.lureActionOptionIcon} resizeMode="contain" />
              <Text style={styles.lureActionOptionText}>{action.label}</Text>
            </Pressable>
          )
        })}
      </View>
    </View>
  )
}

function fieldsLabel(copy, key) {
  return copy.resources.fields[key]
}

function LureInfoLine({ label, value }) {
  if (!hasDetailValue(value)) {
    return null
  }

  return (
    <View style={styles.lureInfoLine}>
      <Text style={styles.lureInfoLabel}>{label}</Text>
      <Text style={styles.lureInfoValue}>{String(value)}</Text>
    </View>
  )
}

function GenericDetail({ detail, copy }) {
  const rows = getDetailRows(detail.group.key, detail.item, detail.data, copy)

  return (
    <View style={styles.detailGrid}>
      {rows.map((row) => (
        <View key={row.label} style={styles.detailRow}>
          <Text style={styles.detailLabel}>{row.label}</Text>
          <Text style={styles.detailValue}>{row.value}</Text>
        </View>
      ))}
    </View>
  )
}

function DetailSection({ title, tone, children }) {
  return (
    <View style={styles.detailSection}>
      <View style={styles.detailSectionHeader}>
        <View style={[styles.detailSectionBar, { backgroundColor: tone }]} />
        <Text style={styles.detailSectionTitle}>{title}</Text>
      </View>
      {children}
    </View>
  )
}

function DetailLine({ label, value }) {
  if (!hasDetailValue(value)) {
    return null
  }

  return (
    <View style={styles.detailLine}>
      <Text style={styles.detailLineLabel}>{label}</Text>
      <Text style={styles.detailLineValue}>{String(value)}</Text>
    </View>
  )
}

function DetailStat({ label, value, tone }) {
  return (
    <View style={styles.detailStat}>
      <View style={[styles.detailStatDot, { backgroundColor: tone }]} />
      <Text style={styles.detailStatLabel}>{label}</Text>
      <Text style={styles.detailStatValue}>{String(value)}</Text>
    </View>
  )
}

function PlanStep({ number, label, value, tone }) {
  return (
    <View style={styles.planStep}>
      <Text style={[styles.planStepNumber, { color: tone }]}>{number}</Text>
      <View style={styles.planStepCopy}>
        <Text style={styles.detailStatLabel}>{label}</Text>
        <Text style={styles.planStepValue}>{value || '-'}</Text>
      </View>
    </View>
  )
}

function TimelinePoint({ label, value, tone }) {
  return (
    <View style={styles.timelinePoint}>
      <View style={[styles.timelineDot, { backgroundColor: tone }]} />
      <Text style={styles.detailStatLabel}>{label}</Text>
      <Text style={styles.timelineValue}>{value}</Text>
    </View>
  )
}

function ScoreBar({ label, value, kind, copy }) {
  const selected = getLureLevelOption(kind, value, copy)
  const fill = selected?.score || 0
  const color = selected?.color || '#afc0b7'
  const displayValue = selected?.label || value || '-'

  return (
    <View style={styles.scoreBlock}>
      <View style={styles.scoreHeader}>
        <Text style={styles.detailStatLabel}>{label}</Text>
        <Text style={[styles.scoreValue, { color }]}>{displayValue}</Text>
      </View>
      <View style={styles.scoreTrack}>
        <View style={[styles.scoreFill, { width: `${fill * 100}%`, backgroundColor: color }]} />
      </View>
    </View>
  )
}

function PaginationControls({ group, onPageChange, copy }) {
  const page = group.page ?? 0
  const totalPages = Math.max(group.totalPages ?? 1, 1)
  const hasPrevious = group.hasPrevious || page > 0
  const hasNext = group.hasNext || page + 1 < totalPages

  return (
    <View style={styles.paginationControls}>
      <Pressable
        accessibilityRole="button"
        accessibilityLabel={copy.resources.previous}
        disabled={!hasPrevious}
        onPress={() => onPageChange(Math.max(page - 1, 0))}
        style={[styles.pageButton, !hasPrevious && styles.pageButtonDisabled]}
      >
        <Text style={[styles.pageButtonText, !hasPrevious && styles.pageButtonTextDisabled]}>
          {copy.resources.previous}
        </Text>
      </Pressable>

      <Text style={styles.pageIndicator}>
        {copy.resources.page} {page + 1}/{totalPages}
      </Text>

      <Pressable
        accessibilityRole="button"
        accessibilityLabel={copy.resources.next}
        disabled={!hasNext}
        onPress={() => onPageChange(page + 1)}
        style={[styles.pageButton, !hasNext && styles.pageButtonDisabled]}
      >
        <Text style={[styles.pageButtonText, !hasNext && styles.pageButtonTextDisabled]}>
          {copy.resources.next}
        </Text>
      </Pressable>
    </View>
  )
}

function ResourceCard({ item, group, copy, onPress }) {
  const display = getItemDisplay(item, group.key, copy)
  const image = display.image || group.image
  const tone = groupTones[group.key] || groupTones.spots
  const visualCard = group.key === 'catches' || group.key === 'fish' || group.key === 'lureLibrary'

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`${copy.resources.viewDetails}: ${display.title}`}
      onPress={() => onPress(item, group)}
      style={[
        styles.resourceCard,
        visualCard && styles.resourceCardVisual,
        group.key === 'spots' && styles.resourceCardSpot,
        group.key === 'plans' && styles.resourceCardPlan,
        group.key === 'sessions' && styles.resourceCardSession,
        group.key === 'lureBox' && styles.resourceCardInventory,
        { backgroundColor: tone.backgroundColor, borderColor: tone.borderColor },
      ]}
    >
      <View
        style={[
          styles.resourceImageFrame,
          visualCard && styles.resourceImageFrameVisual,
          { backgroundColor: tone.imageBackground },
        ]}
      >
        <Image
          source={{ uri: image }}
          style={[styles.resourceImage, visualCard && styles.resourceImageVisual]}
          resizeMode="cover"
        />
      </View>
      <View style={styles.resourceCardBody}>
        <Text style={styles.resourceTitle}>{display.title}</Text>
        <Text style={styles.resourceMeta}>{display.meta}</Text>
        <Text style={styles.resourceDetail}>{display.detail}</Text>
        <View style={styles.resourceCardFooter}>
          <Text style={[styles.resourceCardAction, { color: tone.accent }]}>{copy.resources.viewDetails}</Text>
          {display.badge && (
            <View style={[styles.resourceBadge, { backgroundColor: tone.imageBackground, borderColor: tone.borderColor }]}>
              <Text style={[styles.resourceBadgeText, { color: tone.accent }]}>{display.badge}</Text>
            </View>
          )}
        </View>
      </View>
    </Pressable>
  )
}

function ProfileScreen({ compact, copy }) {
  return (
    <View style={styles.resourceStack}>
      <ProfileWorkspaceHeader compact={compact} copy={copy} />

      <View style={[styles.profileBoard, compact && styles.profileBoardCompact]}>
        <View style={styles.profileSettingsHeading}>
          <Text style={styles.profileOverline}>{copy.resources.profileReady}</Text>
          <Text style={styles.profileName}>{copy.languageLabel}</Text>
        </View>
        <View style={[styles.profileSettings, styles.profileSettingsOnly]}>
          <ProfileSetting image={profileIcon} label={copy.resources.profileReady} value={copy.menu.profile} />
          <ProfileSetting image={galleryIcon} label={copy.languageLabel} value="PT / EN" />
          <ProfileSetting image={appIcon} label={copy.resources.noAuth} value="MVP" />
        </View>
      </View>
    </View>
  )
}

function ProfileSetting({ image, label, value }) {
  return (
    <View style={styles.profileSetting}>
      <Image source={{ uri: image }} style={styles.profileSettingImage} resizeMode="cover" />
      <View style={styles.profileSettingCopy}>
        <Text style={styles.profileSettingLabel}>{label}</Text>
        <Text style={styles.profileSettingValue}>{value}</Text>
      </View>
    </View>
  )
}

function buildSectionUrl(query, search, page, filters = {}) {
  const params = new URLSearchParams({
    ...(query.params || {}),
    ...Object.fromEntries(Object.entries(filters).filter(([, value]) => value && value !== 'ALL')),
    page: String(Math.max(page, 0)),
    size: String(query.size || 8),
  })

  if (search.trim()) {
    params.set('q', search.trim())
  }

  return `${query.path}?${params.toString()}`
}

function buildDetailUrl(group, item) {
  const id = getItemId(item, group.key)

  switch (group.key) {
    case 'catches':
      return item.sessionId ? `/api/sessions/${item.sessionId}` : null
    case 'spots':
      return `/api/spots/${id}`
    case 'plans':
      return `/api/plans/${id}`
    case 'sessions':
      return `/api/sessions/${id}`
    case 'lureBox':
      return `/api/lure-box/${id}`
    case 'fish':
      return `/api/fish/${id}`
    case 'lureLibrary':
      return `/api/lure-library/${id}`
    default:
      return null
  }
}

function getDetailImage(group, item, data, display) {
  return getImageSource(data?.imageUrl || item.photoThumbnailUrl || item.photoUrl, display.image || group.image)
}

function getDetailRows(groupKey, item, data, copy) {
  const source = data || item
  const fields = copy.resources.fields

  const rowsByGroup = {
    catches: [
      detailRow(fields.size, formatCatchSize(item)),
      detailRow(fields.weight, formatCatchWeight(item)),
      detailRow(fields.spot, item.spotName || data?.spotName),
      detailRow(fields.session, formatSchedule(data?.date || item.sessionDate, data?.startTime || item.sessionStartTime, copy)),
    ],
    spots: [
      detailRow(fields.waterType, source.waterType),
      detailRow(fields.favoriteSpecies, source.favoriteSpecies),
      detailRow(fields.coordinates, formatCoordinates(source.latitude, source.longitude)),
      detailRow(fields.description, source.description),
      detailRow(fields.createdAt, formatDateTime(source.createdAt)),
    ],
    plans: [
      detailRow(fields.spot, source.spotName),
      detailRow(fields.plannedFor, formatSchedule(source.plannedDate, source.plannedTime, copy)),
      detailRow(fields.targetSpecies, source.targetSpecies),
      detailRow(fields.waterClarity, source.waterClarity),
      detailRow(fields.waterLevel, source.waterLevel),
      detailRow(fields.notes, source.notes),
      detailRow(fields.createdAt, formatDateTime(source.createdAt)),
    ],
    sessions: [
      detailRow(fields.spot, source.spotName),
      detailRow('Plan ID', source.planId),
      detailRow(fields.session, formatSchedule(source.date, source.startTime, copy)),
      detailRow(fields.status, source.status),
      detailRow(fields.targetSpecies, source.targetSpecies),
      detailRow(fields.waterClarity, source.waterClarity),
      detailRow(fields.waterLevel, source.waterLevel),
      detailRow(fields.notes, source.notes),
      detailRow(copy.dashboard.successChip, formatBoolean(source.success, copy)),
      detailRow(fields.duration, formatDuration(source.durationMinutes)),
      detailRow(fields.result, source.resultSummary),
      detailRow(fields.finalNotes, source.finalNotes),
      detailRow(fields.rating, formatRating(source.rating)),
    ],
    lureBox: [
      detailRow(fields.type, source.type),
      detailRow(fields.brand, source.brand),
      detailRow(fields.color, source.color),
      detailRow(fields.size, source.size),
      detailRow(fields.weight, source.weight),
      detailRow(fields.quantity, source.quantity),
      detailRow(fields.condition, source.condition),
      detailRow(fields.active, formatBoolean(source.active, copy)),
      detailRow(copy.resources.groups.lureLibrary, source.libraryItemName),
      detailRow(fields.targetSpecies, source.targetSpecies),
      detailRow(fields.waterType, source.waterType),
      detailRow(fields.personalNotes, source.personalNotes),
      detailRow(fields.notes, source.notes),
      detailRow(fields.createdAt, formatDateTime(source.createdAt)),
    ],
    fish: [
      detailRow(fields.description, source.description),
      detailRow(fields.habitat, source.habitatNotes),
      detailRow(fields.activeTimes, source.activeTimes),
      detailRow(fields.strikeZone, formatOptionList(source.strikeZone, getFishStrikeZoneOptions(copy))),
      detailRow(fields.zones, formatOptionList(source.commonZones, getFishCommonZoneOptions(copy))),
      detailRow(fields.favoriteLures, formatListText(source.favoriteLures)),
      detailRow(fields.createdAt, formatDateTime(source.createdAt)),
    ],
    lureLibrary: [
      detailRow(fields.type, source.type),
      detailRow(fields.difficulty, source.difficulty),
      detailRow(fields.effectiveness, source.effectiveness),
      detailRow(fields.action, source.actionType),
      detailRow(fields.description, source.description),
      detailRow(fields.notes, source.usageNotes),
      detailRow(fields.idealConditions, source.idealConditions),
      detailRow(fields.createdAt, formatDateTime(source.createdAt)),
    ],
  }

  return (rowsByGroup[groupKey] || []).filter(Boolean)
}

function detailRow(label, value) {
  if (!hasDetailValue(value)) {
    return null
  }

  return {
    label,
    value: String(value),
  }
}

function hasDetailValue(value) {
  return value !== null && value !== undefined && value !== ''
}

function getLureActionOptions(source, copy) {
  const searchText = [source.name, source.type, source.actionType, source.idealConditions, source.usageNotes]
    .filter(hasDetailValue)
    .join(' ')
    .toLowerCase()

  return lureActionCatalog
    .map((action) => {
      const score = action.terms.reduce((total, term) => total + (searchText.includes(term) ? 1 : 0), 0)
      const content = copy.resources.actionTypes[action.id] || {}

      return {
        ...action,
        ...content,
        score,
      }
    })
    .sort((left, right) => right.score - left.score)
}

function getLureLevelOptions(kind, copy) {
  const labels = copy.resources.levelOptions

  if (kind === 'difficulty') {
    return [
      { value: 'Easy', label: labels.easy, score: 0.34, color: '#2b8c68' },
      { value: 'Medium', label: labels.medium, score: 0.62, color: '#c58a2b' },
      { value: 'Hard', label: labels.hard, score: 0.9, color: '#c45151' },
    ]
  }

  return [
    { value: 'Low', label: labels.low, score: 0.34, color: '#c45151' },
    { value: 'Medium', label: labels.medium, score: 0.62, color: '#c58a2b' },
    { value: 'High', label: labels.high, score: 0.9, color: '#2c76c7' },
  ]
}

function getLureLevelOption(kind, value, copy) {
  const normalized = String(value || '').trim().toLowerCase()

  return getLureLevelOptions(kind, copy).find((option) => option.value.toLowerCase() === normalized || option.label.toLowerCase() === normalized)
}

function getFishStrikeZoneOptions(copy) {
  const labels = copy.resources.fishZoneOptions

  return [
    { value: 'surface', label: labels.surface },
    { value: 'mid-water', label: labels.midWater },
    { value: 'bottom', label: labels.bottom },
    { value: 'vegetation', label: labels.vegetation },
    { value: 'structure', label: labels.structure },
    { value: 'bank', label: labels.bank },
  ]
}

function getFishCommonZoneOptions(copy) {
  const labels = copy.resources.commonZoneOptions

  return [
    { value: 'shallows', label: labels.shallows },
    { value: 'deep-water', label: labels.deepWater },
    { value: 'drop-offs', label: labels.dropOffs },
    { value: 'vegetation', label: labels.vegetation },
    { value: 'rocky-areas', label: labels.rockyAreas },
    { value: 'structures', label: labels.structures },
    { value: 'current', label: labels.current },
    { value: 'open-water', label: labels.openWater },
  ]
}

function normalizeWaterEnvironment(value) {
  const normalized = String(value || '').trim().toUpperCase()

  return normalized === 'SALTWATER' || normalized === 'FRESHWATER' ? normalized : 'UNKNOWN'
}

function getWaterEnvironmentLabel(value, copy) {
  const environment = normalizeWaterEnvironment(value)

  if (environment === 'SALTWATER') {
    return copy.resources.saltwater
  }

  if (environment === 'FRESHWATER') {
    return copy.resources.freshwater
  }

  return copy.resources.unclassified
}

function getFavoriteLureOptions(items, currentValues) {
  const options = new Map()

  currentValues.forEach((value) => {
    if (value) {
      options.set(value, { value, label: value })
    }
  })

  items.forEach((item) => {
    if (item?.name) {
      options.set(item.name, {
        value: item.name,
        label: item.name,
        image: getImageSource(item.imageUrl, getLureImage(item.name)),
      })
    }
  })

  return Array.from(options.values())
}

function formatOptionList(value, options) {
  const normalizedOptions = options || []

  return parseListValue(value)
    .map((entry) => {
      const option = normalizedOptions.find(
        (candidate) => candidate.value.toLowerCase() === entry.toLowerCase() || candidate.label.toLowerCase() === entry.toLowerCase(),
      )

      return option?.label || entry
    })
    .join(', ')
}

function formatListText(value) {
  return parseListValue(value).join(', ')
}

function formatBoolean(value, copy) {
  if (value === null || value === undefined) {
    return null
  }

  return value ? copy.resources.yes : copy.resources.no
}

function formatRating(value) {
  if (!value) {
    return null
  }

  return `${value}/5`
}

function formatDuration(minutes) {
  if (!minutes && minutes !== 0) {
    return null
  }

  return `${minutes} min`
}

function formatDateTime(value) {
  if (!value) {
    return null
  }

  return String(value).replace('T', ' ').slice(0, 16)
}

function toNullableText(value) {
  const trimmed = value.trim()

  return trimmed ? trimmed : null
}

function toNullableNumber(value) {
  const normalized = String(value ?? '').trim()

  if (!normalized) {
    return null
  }

  const number = Number(normalized.replace(',', '.'))
  return Number.isFinite(number) ? number : null
}

function parseListValue(value) {
  if (Array.isArray(value)) {
    return value.filter(Boolean)
  }

  return String(value || '')
    .split(/[,;\n|·]+/)
    .map((entry) => entry.trim())
    .filter(Boolean)
}

function toNullableTime(value) {
  const trimmed = value.trim()

  if (!trimmed) {
    return null
  }

  return trimmed.length === 5 ? `${trimmed}:00` : trimmed
}

function isIsoDate(value) {
  return /^\d{4}-\d{2}-\d{2}$/.test(value.trim())
}

function getItemId(item, groupKey) {
  return item.id ?? item.catchId ?? item.sessionId ?? item.name ?? `${groupKey}-${item.spotName || 'item'}`
}

function getItemDisplay(item, groupKey, copy) {
  switch (groupKey) {
    case 'catches':
      return {
        title: item.species || copy.dashboard.speciesFallback,
        meta: compactLine(item.spotName, item.sessionDate),
        detail: compactLine(formatCatchSize(item), formatCatchWeight(item)),
        badge: item.sessionRating ? `${item.sessionRating}/5` : null,
        image: getImageSource(item.photoThumbnailUrl || item.photoUrl, getFishImage(item.species)),
      }
    case 'spots':
      return {
        title: item.name || 'Spot',
        meta: compactLine(item.waterType, formatCoordinates(item.latitude, item.longitude)),
        detail: item.favoriteSpecies || copy.dashboard.speciesFallback,
        image: getSpotImage(item),
      }
    case 'plans':
      return {
        title: item.spotName || copy.dashboard.focusFallbackTitle,
        meta: compactLine(formatSchedule(item.plannedDate, item.plannedTime, copy), item.targetSpecies),
        detail: compactLine(item.waterClarity, item.waterLevel),
        image: getSpotImage({ name: item.spotName }),
      }
    case 'sessions':
      return {
        title: item.spotName || copy.menu.session,
        meta: compactLine(formatSchedule(item.date, item.startTime, copy), item.targetSpecies),
        detail:
          item.success == null
            ? copy.resources.status
            : `${copy.dashboard.successChip}: ${item.success ? copy.resources.yes : copy.resources.no}`,
        badge: item.status,
        image: getSpotImage({ name: item.spotName }),
      }
    case 'lureBox':
      return {
        title: item.name || copy.menu.lureBox,
        meta: compactLine(item.type, item.brand, item.color),
        detail: compactLine(`${item.quantity ?? 0}x`, item.condition, item.targetSpecies),
        badge: item.active ? copy.resources.active : copy.resources.inactive,
        image: getLureImage(item.name),
      }
    case 'fish':
      return {
        title: item.name || copy.menu.library,
        meta: formatOptionList(item.strikeZone, getFishStrikeZoneOptions(copy)) || copy.dashboard.noPattern,
        detail: formatListText(item.favoriteLures) || copy.resources.empty,
        image: getImageSource(item.imageUrl, getFishImage(item.name)),
      }
    case 'lureLibrary':
      return {
        title: item.name || copy.menu.library,
        meta: compactLine(item.type, item.actionType),
        detail: compactLine(item.difficulty, item.effectiveness),
        image: getImageSource(item.imageUrl, getLureImage(item.name)),
      }
    default:
      return {
        title: item.name || 'Item',
        meta: '',
        detail: '',
      }
  }
}

function getFishImage(name) {
  const normalized = String(name || '').toLowerCase()

  if (normalized.includes('black') || normalized.includes('bass') || normalized.includes('achig')) {
    return blackBass
  }

  if (normalized.includes('pike') || normalized.includes('lucio')) {
    return pike
  }

  return barbel
}

function getSpotImage(item) {
  const normalized = `${item?.name || ''} ${item?.waterType || ''}`.toLowerCase()

  if (normalized.includes('river') || normalized.includes('rio')) {
    return riverSpot
  }

  if (normalized.includes('dam') || normalized.includes('barragem') || normalized.includes('alqueva')) {
    return damSpot
  }

  if (normalized.includes('harbor') || normalized.includes('marina') || normalized.includes('sea')) {
    return harborSpot
  }

  return lakeSpot
}

function getLureImage(name) {
  const normalized = String(name || '').toLowerCase()

  if (normalized.includes('crank')) {
    return crankbait
  }

  if (normalized.includes('jerk')) {
    return jerkbait
  }

  if (normalized.includes('popper') || normalized.includes('topwater')) {
    return popper
  }

  if (normalized.includes('soft') || normalized.includes('vinil')) {
    return senko
  }

  return spinnerbait
}

function getImageSource(remoteImage, fallback) {
  if (!remoteImage || remoteImage.startsWith('/demo/') || remoteImage.includes('example.com')) {
    return fallback
  }

  return remoteImage
}

function compactLine(...parts) {
  return parts.filter((part) => part !== null && part !== undefined && part !== '').join(' · ') || '-'
}

function formatCoordinates(latitude, longitude) {
  if (latitude == null || longitude == null) {
    return null
  }

  return `${Number(latitude).toFixed(3)}, ${Number(longitude).toFixed(3)}`
}

function formatSizeWeight(item) {
  const value = compactLine(
    formatCatchSize(item),
    formatCatchWeight(item),
  )

  return value === '-' ? null : value
}

function formatCatchSize(item) {
  return item?.sizeCm != null ? `${item.sizeCm} cm` : null
}

function formatCatchWeight(item) {
  return item?.weightKg != null ? `${item.weightKg} kg` : null
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
    backgroundColor: '#f5f8f5',
  },
  contentInner: {
    padding: 30,
    gap: 20,
  },
  topBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    gap: 18,
  },
  galleryTopBar: {
    minHeight: 18,
    justifyContent: 'flex-end',
  },
  topBarCompact: {
    flexDirection: 'column',
  },
  topBarSide: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
    justifyContent: 'flex-end',
    gap: 8,
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
  workspacePill: {
    minHeight: 38,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingHorizontal: 12,
    borderRadius: 999,
    backgroundColor: '#e3f3ee',
    borderWidth: 1,
    borderColor: '#c5e5dc',
  },
  workspacePillDot: {
    width: 8,
    height: 8,
    borderRadius: 8,
    backgroundColor: '#0f766e',
  },
  workspacePillText: {
    color: '#155e59',
    fontSize: 12,
    fontWeight: '900',
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
  commandDashboard: {
    gap: 18,
  },
  commandHero: {
    minHeight: 344,
    overflow: 'hidden',
    borderRadius: 22,
    backgroundColor: '#071d28',
  },
  commandHeroImage: {
    opacity: 0.78,
  },
  commandHeroOverlay: {
    flex: 1,
    justifyContent: 'space-between',
    padding: 28,
    backgroundColor: 'rgba(4, 20, 29, 0.58)',
  },
  commandHeroMain: {
    maxWidth: 780,
    paddingTop: 8,
  },
  commandHeroKicker: {
    color: '#f4d28c',
    fontSize: 12,
    fontWeight: '900',
    letterSpacing: 1.2,
    textTransform: 'uppercase',
  },
  commandHeroTitle: {
    maxWidth: 760,
    marginTop: 10,
    color: '#ffffff',
    fontSize: 40,
    lineHeight: 46,
    fontWeight: '900',
  },
  commandHeroText: {
    maxWidth: 680,
    marginTop: 10,
    color: '#edf7f0',
    fontSize: 16,
    lineHeight: 23,
    fontWeight: '700',
  },
  commandHeroPulses: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 9,
    marginTop: 24,
  },
  commandActionButton: {
    flexGrow: 1,
    flexBasis: 240,
    minHeight: 112,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    padding: 14,
    borderRadius: 16,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d7e2de',
  },
  commandActionImage: {
    width: 66,
    height: 66,
    borderRadius: 14,
    backgroundColor: '#e6ece4',
  },
  commandActionCopy: {
    flex: 1,
  },
  commandActionLabel: {
    color: '#102421',
    fontSize: 17,
    fontWeight: '900',
  },
  commandActionDetail: {
    marginTop: 4,
    color: '#5a6b63',
    fontSize: 13,
    lineHeight: 18,
    fontWeight: '700',
  },
  commandActionArrow: {
    color: '#11a89c',
    fontSize: 25,
    lineHeight: 28,
    fontWeight: '900',
  },
  commandStat: {
    flexGrow: 1,
    flexBasis: 130,
    minHeight: 98,
    justifyContent: 'space-between',
    padding: 16,
    borderRadius: 16,
    backgroundColor: '#d9f7f0',
    borderWidth: 1,
    borderColor: '#aee4d8',
  },
  commandStatGold: {
    backgroundColor: '#fff3cc',
    borderColor: '#ead28a',
  },
  commandStatViolet: {
    backgroundColor: '#eee7ff',
    borderColor: '#d2c2f0',
  },
  commandStatPink: {
    backgroundColor: '#ffe8ef',
    borderColor: '#efc1d0',
  },
  commandStatValue: {
    color: '#082f3f',
    fontSize: 31,
    lineHeight: 35,
    fontWeight: '900',
  },
  commandStatLabel: {
    marginTop: 8,
    color: '#27615d',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  commandMetricPanel: {
    flexGrow: 1,
    flexBasis: 320,
    minHeight: 168,
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 14,
    padding: 18,
    borderRadius: 18,
    backgroundColor: '#f8fbf6',
    borderWidth: 1,
    borderColor: '#d3dfd8',
  },
  commandListPanel: {
    flexGrow: 1,
    flexBasis: 320,
    minHeight: 162,
    flexDirection: 'row',
    gap: 14,
    padding: 15,
    borderRadius: 18,
    backgroundColor: '#102f3a',
    borderWidth: 1,
    borderColor: '#245460',
  },
  commandMetricCopy: {
    flex: 1,
    justifyContent: 'center',
  },
  commandListCopy: {
    flex: 1,
    justifyContent: 'center',
  },
  commandListLabel: {
    color: '#83d7cf',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  commandListTitle: {
    marginTop: 8,
    color: '#ffffff',
    fontSize: 21,
    lineHeight: 26,
    fontWeight: '900',
  },
  commandListText: {
    marginTop: 7,
    color: '#d9eeeb',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '600',
  },
  commandPanelLabel: {
    color: '#3f766f',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  commandPanelTitle: {
    marginTop: 8,
    color: '#102421',
    fontSize: 21,
    lineHeight: 26,
    fontWeight: '900',
  },
  commandPanelText: {
    marginTop: 7,
    color: '#5a6b63',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '600',
  },
  commandMetricImage: {
    width: 98,
    height: 98,
    borderRadius: 14,
    backgroundColor: '#e6ece4',
  },
  commandListImage: {
    width: 102,
    minHeight: 102,
    borderRadius: 14,
    backgroundColor: '#285765',
  },
  workspaceCompact: {
    minHeight: 178,
  },
  atlasWorkspace: {
    minHeight: 238,
    flexDirection: 'row',
    overflow: 'hidden',
    borderRadius: 20,
    backgroundColor: '#0a3441',
  },
  atlasWorkspaceVisual: {
    flex: 1.45,
    minHeight: 238,
  },
  atlasWorkspaceImage: {
    opacity: 0.88,
  },
  atlasWorkspaceOverlay: {
    flex: 1,
    justifyContent: 'flex-end',
    padding: 24,
    backgroundColor: 'rgba(4, 30, 38, 0.42)',
  },
  workspaceOverline: {
    color: '#f4d28c',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 1,
    textTransform: 'uppercase',
  },
  workspaceHeroTitle: {
    marginTop: 8,
    color: '#ffffff',
    fontSize: 30,
    lineHeight: 35,
    fontWeight: '900',
  },
  workspaceHeroText: {
    maxWidth: 420,
    marginTop: 7,
    color: '#e6f5f0',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
  },
  atlasWorkspaceInfo: {
    width: 244,
    justifyContent: 'center',
    padding: 22,
    backgroundColor: '#eff8f3',
  },
  workspaceInfoHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 10,
  },
  workspaceInfoLabel: {
    maxWidth: 125,
    color: '#3d6f67',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.8,
    textTransform: 'uppercase',
  },
  workspaceInfoCount: {
    color: '#0b5d5a',
    fontSize: 34,
    lineHeight: 36,
    fontWeight: '900',
  },
  workspaceInfoText: {
    marginTop: 16,
    color: '#47655f',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '700',
  },
  workspaceRule: {
    height: 1,
    marginVertical: 18,
    backgroundColor: '#bedbd0',
  },
  workspaceInfoFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 8,
  },
  workspaceFooterLabel: {
    color: '#6d7b75',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  workspaceFooterValue: {
    color: '#0b5d5a',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  missionWorkspace: {
    minHeight: 208,
    flexDirection: 'row',
    overflow: 'hidden',
    borderRadius: 20,
    backgroundColor: '#17243b',
  },
  missionWorkspaceRail: {
    width: 72,
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 24,
    backgroundColor: '#f0bf58',
  },
  missionWorkspaceCode: {
    color: '#17243b',
    fontSize: 12,
    fontWeight: '900',
    letterSpacing: 1,
  },
  missionWorkspaceLine: {
    width: 1,
    flex: 1,
    marginVertical: 12,
    backgroundColor: 'rgba(23, 36, 59, 0.45)',
  },
  missionWorkspaceBody: {
    flex: 1,
    justifyContent: 'space-between',
    padding: 24,
  },
  missionWorkspaceTop: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 16,
  },
  missionWorkspaceTitle: {
    marginTop: 8,
    color: '#ffffff',
    fontSize: 30,
    lineHeight: 35,
    fontWeight: '900',
  },
  missionWorkspaceCount: {
    minWidth: 76,
    alignItems: 'flex-end',
  },
  missionWorkspaceCountValue: {
    color: '#f0bf58',
    fontSize: 34,
    lineHeight: 36,
    fontWeight: '900',
  },
  missionWorkspaceCountLabel: {
    marginTop: 3,
    color: '#aebdce',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  missionWorkspaceText: {
    maxWidth: 500,
    marginTop: 13,
    color: '#d7e2f0',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
  },
  missionTrack: {
    maxWidth: 400,
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 18,
  },
  missionTrackPoint: {
    alignItems: 'center',
  },
  missionTrackDot: {
    width: 34,
    height: 34,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 34,
    backgroundColor: '#31445d',
    borderWidth: 1,
    borderColor: '#536a85',
  },
  missionTrackDotActive: {
    backgroundColor: '#f0bf58',
    borderColor: '#f0bf58',
  },
  missionTrackDotText: {
    color: '#cad6e3',
    fontSize: 12,
    fontWeight: '900',
  },
  missionTrackDotTextActive: {
    color: '#17243b',
  },
  missionTrackLine: {
    width: 54,
    height: 1,
    marginHorizontal: 6,
    backgroundColor: '#64758c',
  },
  liveWorkspace: {
    minHeight: 222,
    overflow: 'hidden',
    borderRadius: 20,
    backgroundColor: '#082f3f',
  },
  liveWorkspaceImage: {
    opacity: 0.72,
  },
  liveWorkspaceOverlay: {
    flex: 1,
    padding: 24,
    backgroundColor: 'rgba(3, 24, 34, 0.58)',
  },
  liveWorkspaceTop: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
  },
  liveSignal: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  liveSignalDot: {
    width: 9,
    height: 9,
    borderRadius: 9,
    backgroundColor: '#49e0a3',
  },
  liveSignalText: {
    color: '#a8f3d1',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 1,
    textTransform: 'uppercase',
  },
  liveSessionCount: {
    color: '#d9eeeb',
    fontSize: 12,
    fontWeight: '900',
  },
  liveWorkspaceTitle: {
    marginTop: 43,
    color: '#ffffff',
    fontSize: 34,
    lineHeight: 39,
    fontWeight: '900',
  },
  liveWorkspaceText: {
    maxWidth: 520,
    marginTop: 6,
    color: '#d8efea',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
  },
  livePulseRow: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    gap: 6,
    marginTop: 22,
  },
  livePulseBar: {
    width: 7,
    borderRadius: 7,
    backgroundColor: '#49e0a3',
  },
  livePulseShort: {
    height: 12,
  },
  livePulseTall: {
    height: 28,
  },
  livePulseMedium: {
    height: 20,
  },
  livePulseLabel: {
    marginLeft: 5,
    color: '#a8f3d1',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  gearWorkspace: {
    minHeight: 190,
    flexDirection: 'row',
    gap: 18,
    padding: 14,
    borderRadius: 20,
    backgroundColor: '#241c2a',
  },
  gearWorkspaceImageFrame: {
    width: 230,
    minHeight: 162,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
    borderRadius: 14,
    backgroundColor: '#f1d2e6',
  },
  gearWorkspaceImage: {
    width: '100%',
    height: '100%',
  },
  gearWorkspaceBody: {
    flex: 1,
    justifyContent: 'center',
    paddingVertical: 8,
  },
  gearWorkspaceTitle: {
    marginTop: 7,
    color: '#ffffff',
    fontSize: 31,
    lineHeight: 36,
    fontWeight: '900',
  },
  gearWorkspaceText: {
    maxWidth: 520,
    marginTop: 7,
    color: '#e9dce7',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
  },
  gearWorkspaceShelf: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 20,
    marginTop: 18,
  },
  gearShelfValue: {
    minWidth: 82,
  },
  gearShelfLabel: {
    color: '#caa9c5',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  gearShelfNumber: {
    marginTop: 2,
    color: '#f4c5df',
    fontSize: 18,
    fontWeight: '900',
  },
  profileWorkspace: {
    minHeight: 224,
    flexDirection: 'row',
    gap: 18,
    padding: 20,
    overflow: 'hidden',
    borderRadius: 20,
    backgroundColor: '#122c48',
    borderWidth: 1,
    borderColor: '#2d5275',
  },
  profileWorkspaceCompact: {
    flexDirection: 'column',
  },
  profileWorkspaceIdentity: {
    flexGrow: 1,
    flexBasis: 390,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 18,
    padding: 12,
    borderRadius: 16,
    backgroundColor: '#1a3b5d',
  },
  profileWorkspaceAvatarFrame: {
    width: 126,
    height: 126,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 16,
    backgroundColor: '#f2d28e',
  },
  profileWorkspaceAvatar: {
    width: 108,
    height: 108,
    borderRadius: 13,
  },
  profileWorkspaceCopy: {
    flex: 1,
  },
  profileWorkspaceOverline: {
    color: '#f2d28e',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 1,
    textTransform: 'uppercase',
  },
  profileWorkspaceTitle: {
    marginTop: 8,
    color: '#ffffff',
    fontSize: 32,
    lineHeight: 37,
    fontWeight: '900',
  },
  profileWorkspaceText: {
    maxWidth: 420,
    marginTop: 7,
    color: '#d9e8f4',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
  },
  profileWorkspaceStatus: {
    width: 210,
    justifyContent: 'center',
    padding: 18,
    borderRadius: 16,
    backgroundColor: '#f4f8fb',
  },
  profileStatusHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 10,
  },
  profileStatusLabel: {
    color: '#45647e',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  profileStatusValue: {
    color: '#2168a6',
    fontSize: 24,
    fontWeight: '900',
  },
  profileStatusRule: {
    height: 1,
    marginVertical: 18,
    backgroundColor: '#cfdfeb',
  },
  profileStatusHint: {
    color: '#2168a6',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
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
  signalTileGold: {
    backgroundColor: '#fff6dc',
    borderColor: '#efd99c',
  },
  signalTileViolet: {
    backgroundColor: '#f2eafb',
    borderColor: '#dac6ee',
  },
  signalTilePink: {
    backgroundColor: '#ffedf3',
    borderColor: '#efcbd9',
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
  profileBoard: {
    maxWidth: '100%',
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 16,
    padding: 16,
    borderRadius: 14,
    backgroundColor: '#f0f6ff',
    borderWidth: 1,
    borderColor: '#c5dcef',
  },
  profileBoardCompact: {
    flexDirection: 'column',
  },
  profileIdentity: {
    flexGrow: 1,
    flexBasis: 330,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    padding: 10,
    borderRadius: 11,
    backgroundColor: '#ffffff',
  },
  profileAvatarFrame: {
    width: 108,
    height: 108,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 14,
    backgroundColor: '#d9e9fb',
  },
  profileAvatar: {
    width: 92,
    height: 92,
    borderRadius: 12,
  },
  profileIdentityCopy: {
    flex: 1,
  },
  profileOverline: {
    color: '#2c76c7',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  profileName: {
    marginTop: 5,
    color: '#102421',
    fontSize: 25,
    fontWeight: '900',
  },
  profileDescription: {
    marginTop: 7,
    color: '#53645d',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '600',
  },
  profileSettings: {
    flexGrow: 1,
    flexBasis: 330,
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 9,
  },
  profileSettingsHeading: {
    width: '100%',
    paddingHorizontal: 4,
  },
  profileSettingsOnly: {
    flexBasis: '100%',
  },
  profileSetting: {
    flexGrow: 1,
    flexBasis: 130,
    minHeight: 92,
    justifyContent: 'center',
    gap: 7,
    padding: 10,
    borderRadius: 10,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d5e5f4',
  },
  profileSettingImage: {
    width: 32,
    height: 32,
    borderRadius: 7,
  },
  profileSettingCopy: {
    minWidth: 0,
  },
  profileSettingLabel: {
    color: '#6d7b75',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  profileSettingValue: {
    marginTop: 3,
    color: '#102421',
    fontSize: 14,
    fontWeight: '900',
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
    maxWidth: '100%',
    minHeight: 198,
    flexDirection: 'row',
    alignItems: 'stretch',
    gap: 16,
    padding: 14,
    borderRadius: 14,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  featurePanelCompact: {
    flexDirection: 'column',
    alignItems: 'stretch',
  },
  featurePanelCompactAccent: {
    width: '100%',
    height: 6,
  },
  featureAccent: {
    width: 6,
    borderRadius: 8,
  },
  featureVisual: {
    width: 172,
    minHeight: 168,
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
    overflow: 'hidden',
    borderRadius: 12,
  },
  featurePanelCompactVisual: {
    width: '100%',
    minHeight: 170,
  },
  featureVisualTag: {
    position: 'absolute',
    left: 10,
    bottom: 10,
    paddingHorizontal: 8,
    paddingVertical: 5,
    borderRadius: 999,
  },
  featureVisualTagText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  featureImage: {
    width: 142,
    height: 142,
    borderRadius: 12,
  },
  featureCopy: {
    flex: 1,
    justifyContent: 'center',
  },
  featureTitle: {
    marginTop: 6,
    color: '#102421',
    fontSize: 30,
    lineHeight: 35,
    fontWeight: '900',
  },
  featureText: {
    maxWidth: 520,
    marginTop: 7,
    color: '#5a6b63',
    fontSize: 16,
    lineHeight: 23,
    fontWeight: '600',
  },
  featureMetaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 7,
    marginTop: 16,
  },
  featureMetaDot: {
    width: 8,
    height: 8,
    borderRadius: 8,
  },
  featureMetaText: {
    color: '#53645d',
    fontSize: 13,
    fontWeight: '900',
  },
  galleryScreen: {
    gap: 22,
    paddingVertical: 8,
    paddingHorizontal: 20,
    backgroundColor: '#f7f1eb',
    borderRadius: 24,
    borderWidth: 1,
    borderColor: '#eadbd2',
  },
  galleryScreenCompact: {
    paddingHorizontal: 10,
    gap: 16,
  },
  galleryShowcaseHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 20,
    padding: 20,
    borderRadius: 16,
    backgroundColor: '#fffdfb',
    borderWidth: 1,
    borderColor: '#eee0d8',
    borderLeftWidth: 6,
    borderLeftColor: '#e66f51',
  },
  galleryShowcaseHeaderCompact: {
    flexDirection: 'column',
    alignItems: 'stretch',
  },
  galleryHeaderCopy: {
    flex: 1,
    alignItems: 'flex-start',
  },
  galleryShowcaseEyebrow: {
    color: '#b95e47',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 1.2,
    textTransform: 'uppercase',
  },
  galleryShowcaseTitle: {
    color: '#25252c',
    fontSize: 31,
    lineHeight: 37,
    fontWeight: '900',
    letterSpacing: 0.8,
    textTransform: 'uppercase',
  },
  galleryShowcaseSubtitle: {
    maxWidth: 620,
    marginTop: 8,
    color: '#666679',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
  },
  galleryHeaderMetaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: 7,
    marginTop: 14,
  },
  galleryHeaderMetaDot: {
    width: 8,
    height: 8,
    borderRadius: 8,
    backgroundColor: '#e66f51',
  },
  galleryHeaderMetaText: {
    color: '#856b61',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.2,
  },
  galleryHeaderMetaDivider: {
    color: '#d3a693',
    fontSize: 14,
    fontWeight: '900',
  },
  galleryHeaderVisual: {
    minWidth: 182,
    minHeight: 98,
    flexDirection: 'row',
    alignItems: 'center',
    position: 'relative',
    overflow: 'hidden',
    padding: 10,
    borderRadius: 12,
    backgroundColor: '#123f4d',
  },
  galleryHeaderVisualStripe: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    left: 0,
    width: 8,
    backgroundColor: '#e66f51',
  },
  galleryHeaderVisualBlock: {
    position: 'absolute',
    top: -18,
    right: 36,
    width: 72,
    height: 136,
    transform: [{ rotate: '18deg' }],
    backgroundColor: '#1b5d6b',
  },
  galleryHeaderVisualIcon: {
    width: 62,
    height: 62,
    marginLeft: 8,
    zIndex: 1,
  },
  galleryHeaderVisualCaption: {
    zIndex: 1,
    marginLeft: 10,
  },
  galleryHeaderVisualNumber: {
    color: '#ffffff',
    fontSize: 27,
    lineHeight: 29,
    fontWeight: '900',
  },
  galleryHeaderVisualLabel: {
    marginTop: 3,
    color: '#b9e3df',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  galleryCreateButton: {
    minHeight: 48,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingHorizontal: 17,
    borderRadius: 5,
    backgroundColor: '#b95e47',
    shadowColor: '#b95e47',
    shadowOpacity: 0.18,
    shadowRadius: 7,
    shadowOffset: { width: 0, height: 3 },
  },
  galleryCaptureActionRow: {
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: -6,
    marginBottom: -3,
  },
  galleryCreateButtonMark: {
    color: '#ffffff',
    fontSize: 22,
    lineHeight: 22,
    fontWeight: '500',
  },
  galleryCreateButtonText: {
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  galleryActions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    gap: 10,
    marginTop: 14,
  },
  galleryAction: {
    minWidth: 112,
    minHeight: 40,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 18,
    borderRadius: 4,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d5d5e5',
  },
  galleryActionSelected: {
    backgroundColor: '#1d1d25',
    borderColor: '#1d1d25',
  },
  galleryActionText: {
    color: '#3a3a48',
    fontSize: 12,
    fontWeight: '900',
    letterSpacing: 0.3,
    textTransform: 'uppercase',
  },
  galleryActionTextSelected: {
    color: '#ffffff',
  },
  galleryActionHint: {
    color: '#77778b',
    fontSize: 12,
    lineHeight: 17,
    fontWeight: '700',
  },
  galleryToolbar: {
    minHeight: 48,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    paddingHorizontal: 2,
  },
  galleryToolbarCompact: {
    flexDirection: 'column',
    alignItems: 'stretch',
  },
  gallerySearchInput: {
    flex: 1,
    minHeight: 42,
    paddingHorizontal: 12,
    borderRadius: 4,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d5d5e5',
    color: '#25252c',
    fontSize: 14,
    fontWeight: '700',
  },
  galleryClearButton: {
    minHeight: 42,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 15,
    borderRadius: 4,
    backgroundColor: '#1d1d25',
  },
  galleryClearButtonText: {
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  galleryGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: 12,
    paddingHorizontal: 4,
  },
  galleryGridCompact: {
    gap: 10,
    paddingHorizontal: 0,
  },
  galleryTile: {
    flexGrow: 1,
    flexBasis: 190,
    maxWidth: 250,
    overflow: 'hidden',
    borderRadius: 10,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#dddded',
    borderTopWidth: 5,
  },
  galleryTileAccentCoral: {
    borderTopColor: '#e66f51',
  },
  galleryTileAccentTeal: {
    borderTopColor: '#268e9f',
  },
  galleryTileAccentGold: {
    borderTopColor: '#d19b37',
  },
  galleryTileAccentViolet: {
    borderTopColor: '#8465b7',
  },
  galleryTileImageFrame: {
    position: 'relative',
    width: '100%',
    aspectRatio: 1,
    overflow: 'hidden',
    backgroundColor: '#e2e2ef',
  },
  galleryTileLureBadge: {
    position: 'absolute',
    top: 10,
    right: 10,
    maxWidth: '72%',
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    paddingVertical: 4,
    paddingHorizontal: 6,
    borderRadius: 4,
    backgroundColor: 'rgba(255, 255, 255, 0.94)',
  },
  galleryTileLureImage: {
    width: 28,
    height: 22,
  },
  galleryTileLureText: {
    flexShrink: 1,
    color: '#25252c',
    fontSize: 10,
    lineHeight: 13,
    fontWeight: '900',
  },
  galleryTileIndexBadge: {
    position: 'absolute',
    top: 10,
    left: 10,
    minWidth: 28,
    minHeight: 24,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 6,
    borderRadius: 4,
    backgroundColor: 'rgba(18, 63, 77, 0.88)',
  },
  galleryTileIndexText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.4,
  },
  galleryTileImage: {
    width: '100%',
    height: '100%',
  },
  galleryTileOverlay: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    minHeight: 34,
    justifyContent: 'center',
    paddingHorizontal: 10,
    backgroundColor: 'rgba(29, 29, 37, 0.78)',
  },
  galleryTileOverlayText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.4,
    textTransform: 'uppercase',
  },
  galleryTileCaption: {
    minHeight: 94,
    justifyContent: 'center',
    gap: 7,
    padding: 12,
  },
  galleryTileTitleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 7,
  },
  galleryTileArrow: {
    marginLeft: 'auto',
    color: '#b95e47',
    fontSize: 17,
    lineHeight: 17,
    fontWeight: '900',
  },
  galleryTileLocationRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  galleryTileLocationDot: {
    width: 6,
    height: 6,
    borderRadius: 6,
    backgroundColor: '#268e9f',
  },
  galleryTileFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 8,
  },
  galleryTileTitle: {
    color: '#25252c',
    fontSize: 15,
    lineHeight: 19,
    fontWeight: '900',
  },
  galleryTileMeta: {
    color: '#545465',
    fontSize: 12,
    lineHeight: 16,
    fontWeight: '800',
  },
  galleryTileDate: {
    color: '#86869a',
    fontSize: 11,
    lineHeight: 15,
    fontWeight: '700',
  },
  galleryTileMeasure: {
    color: '#b95e47',
    fontSize: 10,
    lineHeight: 14,
    fontWeight: '900',
  },
  galleryEmpty: {
    minHeight: 240,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 28,
    borderRadius: 4,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#dddded',
  },
  galleryEmptyIcon: {
    width: 56,
    height: 56,
    marginBottom: 12,
  },
  galleryEmptyTitle: {
    color: '#25252c',
    fontSize: 17,
    fontWeight: '900',
    textAlign: 'center',
  },
  galleryEmptyText: {
    maxWidth: 420,
    marginTop: 6,
    color: '#666679',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '700',
    textAlign: 'center',
  },
  galleryEditor: {
    gap: 17,
    padding: 18,
    borderRadius: 10,
    backgroundColor: '#fffaf7',
    borderWidth: 1,
    borderColor: '#ecd8d0',
  },
  galleryEditorCompact: {
    padding: 12,
  },
  galleryEditorHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 14,
  },
  galleryEditorEyebrow: {
    color: '#b95e47',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.8,
    textTransform: 'uppercase',
  },
  galleryEditorTitle: {
    marginTop: 4,
    color: '#302628',
    fontSize: 24,
    lineHeight: 29,
    fontWeight: '900',
  },
  galleryEditorHint: {
    maxWidth: 630,
    marginTop: 5,
    color: '#806d68',
    fontSize: 12,
    lineHeight: 17,
    fontWeight: '700',
  },
  galleryEditorCancel: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 12,
    borderRadius: 4,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#e5d4ce',
  },
  galleryEditorCancelText: {
    color: '#6c5e5a',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  galleryEditorPreviewRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  galleryEditorFishPreview: {
    flexGrow: 1,
    flexBasis: 280,
    minHeight: 170,
    position: 'relative',
    overflow: 'hidden',
    borderRadius: 8,
    backgroundColor: '#ece2de',
  },
  galleryEditorFishImage: {
    width: '100%',
    height: 170,
  },
  galleryEditorPreviewOverlay: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    paddingHorizontal: 11,
    paddingVertical: 8,
    backgroundColor: 'rgba(45, 34, 32, 0.7)',
  },
  galleryEditorPreviewLabel: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  galleryEditorLurePreview: {
    flexGrow: 1,
    flexBasis: 180,
    minHeight: 170,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 14,
    borderRadius: 8,
    backgroundColor: '#f4e7df',
    borderWidth: 1,
    borderColor: '#ead1c6',
  },
  galleryEditorLureImage: {
    width: '100%',
    height: 112,
  },
  galleryEditorLureLabel: {
    maxWidth: '100%',
    marginTop: 9,
    color: '#694b42',
    fontSize: 12,
    lineHeight: 16,
    fontWeight: '900',
    textAlign: 'center',
  },
  galleryFormGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  gallerySelect: {
    flexGrow: 1,
    flexBasis: 260,
    position: 'relative',
    zIndex: 2,
  },
  gallerySelectButton: {
    minHeight: 47,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingHorizontal: 12,
    borderRadius: 5,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#e1cbc3',
  },
  gallerySelectButtonOpen: {
    borderColor: '#b95e47',
  },
  gallerySelectSelectedImage: {
    width: 34,
    height: 30,
  },
  gallerySelectButtonText: {
    flex: 1,
    color: '#3d302d',
    fontSize: 13,
    lineHeight: 17,
    fontWeight: '800',
  },
  gallerySelectPlaceholder: {
    color: '#907f79',
  },
  gallerySelectButtonIcon: {
    color: '#b95e47',
    fontSize: 18,
    fontWeight: '800',
  },
  gallerySelectMenu: {
    position: 'absolute',
    top: 72,
    left: 0,
    right: 0,
    maxHeight: 220,
    overflow: 'auto',
    padding: 5,
    borderRadius: 5,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d8bfb6',
    shadowColor: '#6d4b42',
    shadowOpacity: 0.15,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    zIndex: 20,
  },
  gallerySelectOption: {
    minHeight: 42,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingHorizontal: 8,
    borderRadius: 4,
  },
  gallerySelectOptionSelected: {
    backgroundColor: '#f7e9e4',
  },
  gallerySelectOptionImage: {
    width: 34,
    height: 30,
  },
  gallerySelectOptionText: {
    flex: 1,
    color: '#594742',
    fontSize: 12,
    lineHeight: 16,
    fontWeight: '800',
  },
  gallerySelectOptionTextSelected: {
    color: '#a34f3c',
  },
  gallerySelectEmpty: {
    padding: 12,
    color: '#907f79',
    fontSize: 12,
    fontWeight: '700',
  },
  galleryImagePicker: {
    flexGrow: 0,
    flexBasis: 'auto',
    alignSelf: 'stretch',
  },
  libraryScreen: {
    gap: 22,
    paddingVertical: 8,
    paddingHorizontal: 20,
    backgroundColor: '#eff8f3',
    borderRadius: 22,
  },
  libraryScreenCompact: {
    paddingHorizontal: 10,
    gap: 16,
  },
  libraryShowcaseHeader: {
    alignItems: 'center',
    paddingTop: 16,
    paddingBottom: 4,
  },
  libraryShowcaseTitle: {
    color: '#183d35',
    fontSize: 34,
    lineHeight: 40,
    fontWeight: '900',
    letterSpacing: 1.2,
    textAlign: 'center',
    textTransform: 'uppercase',
  },
  libraryShowcaseSubtitle: {
    maxWidth: 620,
    marginTop: 8,
    color: '#54746b',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
    textAlign: 'center',
  },
  librarySwitch: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: 10,
    marginTop: 20,
  },
  librarySwitchButton: {
    minWidth: 164,
    minHeight: 42,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 16,
    borderRadius: 5,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#c7dfd3',
  },
  librarySwitchButtonSelected: {
    backgroundColor: '#183d35',
    borderColor: '#183d35',
  },
  librarySwitchText: {
    color: '#35665a',
    fontSize: 12,
    fontWeight: '900',
    letterSpacing: 0.3,
    textTransform: 'uppercase',
  },
  librarySwitchTextSelected: {
    color: '#ffffff',
  },
  libraryEnvironmentFilter: {
    alignItems: 'center',
    gap: 7,
    marginTop: 15,
  },
  libraryEnvironmentLabel: {
    color: '#54746b',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  libraryEnvironmentOptions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: 7,
  },
  libraryEnvironmentOption: {
    minHeight: 34,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 13,
    borderRadius: 999,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#c7dfd3',
  },
  libraryEnvironmentOptionSelected: {
    backgroundColor: '#2b8c68',
    borderColor: '#2b8c68',
  },
  libraryEnvironmentFreshwaterSelected: {
    backgroundColor: '#1687a7',
    borderColor: '#1687a7',
  },
  libraryEnvironmentSaltwaterSelected: {
    backgroundColor: '#3978bb',
    borderColor: '#3978bb',
  },
  libraryEnvironmentOptionText: {
    color: '#35665a',
    fontSize: 11,
    fontWeight: '900',
  },
  libraryEnvironmentOptionTextSelected: {
    color: '#ffffff',
  },
  libraryCreateButton: {
    minHeight: 42,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 13,
    paddingHorizontal: 17,
    borderRadius: 5,
    backgroundColor: '#2b8c68',
  },
  libraryCreateButtonText: {
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  libraryToolbar: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 2,
    marginTop: -8,
    marginBottom: -4,
  },
  libraryToolbarCompact: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  libraryGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'flex-start',
    gap: 14,
    alignSelf: 'center',
    width: '100%',
    maxWidth: 1070,
    paddingHorizontal: 4,
  },
  libraryGridCompact: {
    gap: 10,
    paddingHorizontal: 0,
  },
  libraryTile: {
    flexGrow: 1,
    flexBasis: 270,
    maxWidth: 330,
    overflow: 'hidden',
    borderRadius: 12,
    backgroundColor: '#ffffff',
    borderWidth: 1,
  },
  libraryTileCompact: {
    maxWidth: '100%',
  },
  libraryFishTile: {
    borderColor: '#c4e3d4',
  },
  libraryLureTile: {
    borderColor: '#c9dcf0',
  },
  libraryTileImageFrame: {
    width: '100%',
    aspectRatio: 1,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  libraryFishImageFrame: {
    backgroundColor: '#e0f2e8',
  },
  libraryLureImageFrame: {
    backgroundColor: '#e2edfb',
  },
  libraryTileImage: {
    width: '92%',
    height: '92%',
  },
  libraryTileBody: {
    minHeight: 148,
    gap: 5,
    padding: 13,
  },
  libraryTileTitle: {
    color: '#183d35',
    fontSize: 19,
    lineHeight: 23,
    fontWeight: '900',
  },
  fishEnvironmentBadge: {
    alignSelf: 'flex-start',
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    paddingHorizontal: 8,
    paddingVertical: 5,
    borderRadius: 999,
  },
  fishEnvironmentBadgeLarge: {
    marginTop: 10,
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  fishEnvironmentBadgeFreshwater: {
    backgroundColor: '#dff2ec',
  },
  fishEnvironmentBadgeSaltwater: {
    backgroundColor: '#dceafa',
  },
  fishEnvironmentBadgeDot: {
    width: 8,
    height: 8,
    borderRadius: 8,
  },
  fishEnvironmentBadgeDotFreshwater: {
    backgroundColor: '#1687a7',
  },
  fishEnvironmentBadgeDotSaltwater: {
    backgroundColor: '#3978bb',
  },
  fishEnvironmentBadgeText: {
    color: '#245c70',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  libraryTileMeta: {
    color: '#35665a',
    fontSize: 12,
    lineHeight: 17,
    fontWeight: '800',
  },
  libraryTileDetail: {
    color: '#61746e',
    fontSize: 12,
    lineHeight: 17,
    fontWeight: '700',
  },
  libraryTileTag: {
    alignSelf: 'flex-start',
    maxWidth: '100%',
    marginTop: 3,
    paddingHorizontal: 8,
    paddingVertical: 5,
    borderRadius: 999,
  },
  libraryFishTag: {
    backgroundColor: '#e1f5ea',
  },
  libraryLureTag: {
    backgroundColor: '#e4effd',
  },
  libraryTileTagText: {
    color: '#35665a',
    fontSize: 10,
    fontWeight: '900',
  },
  libraryTileAction: {
    marginTop: 'auto',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.4,
    textTransform: 'uppercase',
  },
  libraryEmpty: {
    minHeight: 240,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 28,
    borderRadius: 12,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#c7dfd3',
  },
  libraryEmptyIcon: {
    width: 56,
    height: 56,
    marginBottom: 12,
  },
  libraryEmptyTitle: {
    color: '#183d35',
    fontSize: 17,
    fontWeight: '900',
    textAlign: 'center',
  },
  libraryEmptyText: {
    marginTop: 6,
    color: '#54746b',
    fontSize: 13,
    fontWeight: '700',
    textAlign: 'center',
  },
  libraryEditor: {
    gap: 16,
    padding: 16,
    borderRadius: 14,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#c7dfd3',
  },
  libraryEditorCompact: {
    padding: 10,
  },
  libraryEditorHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 14,
  },
  libraryEditorEyebrow: {
    color: '#2b8c68',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.6,
    textTransform: 'uppercase',
  },
  libraryEditorTitle: {
    marginTop: 4,
    color: '#183d35',
    fontSize: 24,
    lineHeight: 29,
    fontWeight: '900',
  },
  libraryEditorCancel: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 12,
    borderRadius: 7,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  libraryEditorCancelText: {
    color: '#102421',
    fontSize: 12,
    fontWeight: '900',
  },
  libraryEditorMain: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'flex-start',
    gap: 16,
  },
  libraryEditorPreviewFrame: {
    flexGrow: 1,
    flexBasis: 230,
    maxWidth: 320,
    aspectRatio: 1,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
    borderRadius: 10,
    backgroundColor: '#e9f3ec',
  },
  libraryEditorPreviewImage: {
    width: '90%',
    height: '90%',
  },
  libraryEditorFields: {
    flexGrow: 1,
    flexBasis: 300,
    alignSelf: 'flex-start',
    gap: 10,
  },
  libraryEditorField: {
    flexGrow: 0,
    flexBasis: 'auto',
    width: '100%',
  },
  libraryEditorSection: {
    gap: 12,
    paddingTop: 13,
    borderTopWidth: 1,
    borderTopColor: '#dce8df',
  },
  libraryEditorSectionHeader: {
    gap: 4,
  },
  libraryEditorSectionTitle: {
    color: '#183d35',
    fontSize: 14,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  libraryEditorSectionHint: {
    marginTop: 4,
    color: '#61746e',
    fontSize: 12,
    lineHeight: 18,
    fontWeight: '700',
  },
  libraryActionPresetGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  libraryActionPreset: {
    flexGrow: 1,
    flexBasis: 130,
    maxWidth: 180,
    minHeight: 105,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 5,
    padding: 7,
    borderRadius: 8,
    backgroundColor: '#eef5fc',
    borderWidth: 1,
    borderColor: '#d7e3f0',
  },
  libraryActionPresetSelected: {
    backgroundColor: '#dfeafa',
    borderColor: '#2c76c7',
  },
  libraryActionPresetImage: {
    width: 58,
    height: 58,
  },
  libraryActionPresetText: {
    color: '#2b5c8e',
    fontSize: 10,
    fontWeight: '900',
    textAlign: 'center',
  },
  libraryActionFiles: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  libraryImagePicker: {
    flexGrow: 1,
    flexBasis: 250,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    padding: 9,
    borderRadius: 9,
    backgroundColor: '#f7faf7',
    borderWidth: 1,
    borderColor: '#dce8df',
  },
  libraryImagePickerStandalone: {
    flexGrow: 0,
    flexShrink: 0,
    alignSelf: 'stretch',
    gap: 4,
    minHeight: 66,
    paddingTop: 2,
  },
  libraryImagePickerPreview: {
    width: 72,
    height: 72,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
    borderRadius: 7,
    backgroundColor: '#e7f0ea',
  },
  libraryImagePickerImage: {
    width: '92%',
    height: '92%',
  },
  libraryImagePickerCopy: {
    flex: 1,
    minWidth: 0,
  },
  libraryImagePickerLabel: {
    color: '#183d35',
    fontSize: 11,
    fontWeight: '900',
  },
  libraryImagePickerHint: {
    marginTop: 3,
    color: '#6d7b75',
    fontSize: 10,
    lineHeight: 15,
    fontWeight: '700',
  },
  libraryImagePickerButton: {
    alignSelf: 'flex-start',
    minHeight: 31,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 7,
    paddingHorizontal: 9,
    borderRadius: 5,
    backgroundColor: '#183d35',
  },
  libraryImagePickerButtonText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  libraryLevelGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  libraryLevelSelect: {
    flexGrow: 1,
    flexBasis: 240,
    gap: 6,
  },
  libraryLevelButton: {
    minHeight: 44,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
    paddingHorizontal: 12,
    borderRadius: 8,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  libraryLevelButtonOpen: {
    borderColor: '#2c76c7',
    backgroundColor: '#f2f7fd',
  },
  libraryLevelButtonText: {
    color: '#102421',
    fontSize: 14,
    fontWeight: '800',
  },
  libraryLevelButtonPlaceholder: {
    color: '#7b8982',
  },
  libraryLevelButtonIcon: {
    color: '#2c76c7',
    fontSize: 18,
    lineHeight: 18,
    fontWeight: '900',
  },
  libraryLevelPreviewTrack: {
    height: 5,
    overflow: 'hidden',
    borderRadius: 5,
    backgroundColor: '#dce6f2',
  },
  libraryLevelPreviewFill: {
    height: 5,
    borderRadius: 5,
  },
  libraryLevelDropdown: {
    gap: 4,
    padding: 4,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#cbdad0',
  },
  libraryLevelOption: {
    minHeight: 38,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingHorizontal: 9,
    borderRadius: 6,
  },
  libraryLevelOptionSelected: {
    backgroundColor: '#e6f0fa',
  },
  libraryLevelOptionDot: {
    width: 9,
    height: 9,
    borderRadius: 9,
  },
  libraryLevelOptionText: {
    color: '#53645d',
    fontSize: 12,
    fontWeight: '800',
  },
  libraryLevelOptionTextSelected: {
    color: '#183d35',
    fontWeight: '900',
  },
  fishEnvironmentField: {
    gap: 6,
  },
  fishEnvironmentFieldHint: {
    color: '#6d7b75',
    fontSize: 10,
    lineHeight: 15,
    fontWeight: '700',
  },
  fishEnvironmentFieldOptions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  fishEnvironmentFieldOption: {
    flexGrow: 1,
    flexBasis: 220,
    minHeight: 52,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 9,
    paddingHorizontal: 13,
    borderRadius: 9,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  fishEnvironmentFieldOptionSelected: {
    backgroundColor: '#dff2ec',
    borderColor: '#1687a7',
  },
  fishEnvironmentFieldOptionSaltwater: {
    backgroundColor: '#dceafa',
    borderColor: '#3978bb',
  },
  fishEnvironmentFieldDot: {
    width: 11,
    height: 11,
    borderRadius: 11,
    backgroundColor: '#1687a7',
  },
  fishEnvironmentFieldDotSaltwater: {
    backgroundColor: '#3978bb',
  },
  fishEnvironmentFieldOptionText: {
    color: '#53645d',
    fontSize: 13,
    fontWeight: '800',
  },
  fishEnvironmentFieldOptionTextSelected: {
    color: '#183d35',
    fontWeight: '900',
  },
  libraryMultiSelectGrid: {
    gap: 10,
  },
  libraryMultiSelect: {
    gap: 6,
  },
  libraryMultiSelectBox: {
    minHeight: 48,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingLeft: 10,
    paddingRight: 6,
    paddingVertical: 6,
    borderRadius: 8,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  librarySelectionChips: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 5,
  },
  librarySelectionChip: {
    minHeight: 28,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    paddingHorizontal: 8,
    borderRadius: 14,
    backgroundColor: '#d8eee2',
    borderWidth: 1,
    borderColor: '#b8d9c6',
  },
  librarySelectionChipText: {
    maxWidth: 240,
    color: '#183d35',
    fontSize: 11,
    fontWeight: '900',
  },
  librarySelectionChipRemove: {
    color: '#2b8c68',
    fontSize: 13,
    lineHeight: 13,
    fontWeight: '900',
  },
  libraryMultiSelectPlaceholder: {
    flex: 1,
    color: '#7b8982',
    fontSize: 13,
    fontWeight: '700',
  },
  libraryMultiSelectToggle: {
    width: 34,
    height: 34,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 6,
    backgroundColor: '#183d35',
  },
  libraryMultiSelectToggleOpen: {
    backgroundColor: '#2c76c7',
  },
  libraryMultiSelectToggleText: {
    color: '#ffffff',
    fontSize: 18,
    lineHeight: 18,
    fontWeight: '900',
  },
  libraryMultiSelectHint: {
    color: '#6d7b75',
    fontSize: 10,
    lineHeight: 15,
    fontWeight: '700',
  },
  libraryMultiSelectDropdown: {
    gap: 4,
    padding: 4,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#cbdad0',
  },
  libraryMultiSelectOption: {
    minHeight: 40,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingHorizontal: 9,
    borderRadius: 6,
  },
  libraryMultiSelectOptionSelected: {
    backgroundColor: '#e6f0fa',
  },
  libraryMultiSelectOptionImage: {
    width: 30,
    height: 30,
  },
  libraryMultiSelectOptionText: {
    flex: 1,
    color: '#53645d',
    fontSize: 12,
    fontWeight: '800',
  },
  libraryMultiSelectOptionTextSelected: {
    color: '#183d35',
    fontWeight: '900',
  },
  libraryMultiSelectOptionMark: {
    color: '#2c76c7',
    fontSize: 17,
    fontWeight: '900',
  },
  libraryMultiSelectEmpty: {
    padding: 10,
    color: '#6d7b75',
    fontSize: 12,
    lineHeight: 17,
    fontWeight: '700',
  },
  resourceStack: {
    gap: 14,
  },
  resourceStackAtlas: {
    gap: 20,
  },
  resourceStackMission: {
    gap: 18,
  },
  resourceStackLive: {
    gap: 20,
  },
  resourceStackGear: {
    gap: 18,
  },
  resourceToolbar: {
    maxWidth: '100%',
    minHeight: 58,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    padding: 10,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  resourceToolbarAtlas: {
    backgroundColor: '#f3faf5',
    borderColor: '#b9d9ca',
    borderRadius: 14,
  },
  resourceToolbarMission: {
    backgroundColor: '#f4f1fa',
    borderColor: '#d7cce6',
    borderRadius: 14,
  },
  resourceToolbarLive: {
    backgroundColor: '#eaf7f5',
    borderColor: '#b8ded8',
    borderRadius: 14,
  },
  resourceToolbarGear: {
    backgroundColor: '#fff4fa',
    borderColor: '#e9c7db',
    borderRadius: 14,
  },
  resourceToolbarCompact: {
    flexDirection: 'column',
    alignItems: 'stretch',
  },
  searchInput: {
    flex: 1,
    minHeight: 42,
    paddingHorizontal: 12,
    borderRadius: 8,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
    color: '#102421',
    fontSize: 15,
    fontWeight: '700',
  },
  secondaryButton: {
    minHeight: 42,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#082f3f',
  },
  secondaryButtonText: {
    color: '#ffffff',
    fontSize: 13,
    fontWeight: '900',
  },
  primaryButton: {
    minHeight: 42,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#11c5b7',
  },
  primaryButtonText: {
    color: '#062e37',
    fontSize: 13,
    fontWeight: '900',
  },
  formPanel: {
    maxWidth: '100%',
    gap: 14,
    padding: 14,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#b8e9df',
  },
  formTitle: {
    marginTop: 6,
    color: '#102421',
    fontSize: 22,
    lineHeight: 27,
    fontWeight: '900',
  },
  formGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  formSection: {
    gap: 8,
  },
  formField: {
    flexGrow: 1,
    flexBasis: 240,
    gap: 6,
  },
  formFieldWide: {
    flexBasis: '100%',
  },
  formLabel: {
    color: '#53645d',
    fontSize: 12,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  formInput: {
    minHeight: 44,
    paddingHorizontal: 12,
    borderRadius: 8,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
    color: '#102421',
    fontSize: 15,
    fontWeight: '700',
  },
  formInputMultiline: {
    minHeight: 88,
    paddingTop: 10,
    textAlignVertical: 'top',
  },
  choiceGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  choiceChip: {
    minHeight: 44,
    justifyContent: 'center',
    gap: 2,
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 8,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  choiceChipSelected: {
    backgroundColor: '#dff8f3',
    borderColor: '#11c5b7',
  },
  choiceChipText: {
    color: '#102421',
    fontSize: 13,
    fontWeight: '900',
  },
  choiceChipTextSelected: {
    color: '#062e37',
  },
  choiceChipMeta: {
    color: '#6d7b75',
    fontSize: 11,
    fontWeight: '800',
  },
  choiceChipMetaSelected: {
    color: '#27615d',
  },
  formFeedback: {
    padding: 12,
    borderRadius: 8,
    backgroundColor: '#fff8ec',
    borderWidth: 1,
    borderColor: '#efc98b',
  },
  formFeedbackSuccess: {
    backgroundColor: '#e8f8ef',
    borderColor: '#a6dfba',
  },
  formFeedbackText: {
    color: '#8b4b19',
    fontSize: 13,
    fontWeight: '900',
  },
  formFeedbackTextSuccess: {
    color: '#166534',
  },
  submitButton: {
    minHeight: 46,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#082f3f',
  },
  submitButtonDisabled: {
    opacity: 0.65,
  },
  submitButtonText: {
    color: '#ffffff',
    fontSize: 14,
    fontWeight: '900',
  },
  detailPanel: {
    maxWidth: '100%',
    gap: 16,
    padding: 12,
    borderRadius: 14,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#b8e9df',
  },
  detailContent: {
    gap: 16,
    padding: 2,
  },
  detailTopbar: {
    minHeight: 102,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    padding: 10,
    borderRadius: 11,
    borderWidth: 1,
  },
  detailTopbarMarker: {
    width: 5,
    height: 70,
    borderRadius: 6,
  },
  detailHeader: {
    minHeight: 86,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  detailImage: {
    width: 82,
    height: 82,
    borderRadius: 12,
    backgroundColor: '#e6ece4',
  },
  detailTitleBlock: {
    flex: 1,
    minWidth: 0,
  },
  detailTitle: {
    marginTop: 5,
    color: '#102421',
    fontSize: 22,
    lineHeight: 27,
    fontWeight: '900',
  },
  detailSubtitle: {
    marginTop: 4,
    color: '#27615d',
    fontSize: 14,
    lineHeight: 19,
    fontWeight: '800',
  },
  detailActionButton: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 10,
    borderRadius: 7,
    backgroundColor: '#e6f1fb',
    borderWidth: 1,
    borderColor: '#c5dcef',
  },
  detailActionButtonText: {
    color: '#2c76c7',
    fontSize: 11,
    fontWeight: '900',
  },
  detailDeleteButton: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 10,
    borderRadius: 7,
    backgroundColor: '#fff0f0',
    borderWidth: 1,
    borderColor: '#efcaca',
  },
  detailDeleteButtonText: {
    color: '#b33d3d',
    fontSize: 11,
    fontWeight: '900',
  },
  detailSectionEyebrow: {
    color: '#6d7b75',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  detailFeatureTitle: {
    marginTop: 5,
    color: '#102421',
    fontSize: 24,
    lineHeight: 29,
    fontWeight: '900',
  },
  detailSection: {
    gap: 10,
    padding: 14,
    borderRadius: 11,
    backgroundColor: '#f7faf7',
    borderWidth: 1,
    borderColor: '#e0e8e1',
  },
  detailSectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  detailSectionBar: {
    width: 4,
    height: 20,
    borderRadius: 4,
  },
  detailSectionTitle: {
    color: '#102421',
    fontSize: 13,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  detailLine: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 16,
    paddingVertical: 7,
    borderBottomWidth: 1,
    borderBottomColor: '#e3ebe4',
  },
  detailLineLabel: {
    flex: 0.7,
    color: '#6d7b75',
    fontSize: 12,
    fontWeight: '900',
  },
  detailLineValue: {
    flex: 1.3,
    color: '#102421',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '700',
    textAlign: 'right',
  },
  detailLongText: {
    color: '#53645d',
    fontSize: 14,
    lineHeight: 21,
    fontWeight: '600',
  },
  fishDetailView: {
    gap: 14,
  },
  fishDetailViewCompact: {
    gap: 10,
  },
  fishDetailTopline: {
    minHeight: 44,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 12,
  },
  fishDetailToplineText: {
    marginTop: 4,
    color: '#6a7c74',
    fontSize: 12,
    fontWeight: '700',
  },
  fishDetailActions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    justifyContent: 'flex-end',
    gap: 7,
  },
  fishDetailActionButton: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 10,
    borderRadius: 7,
    backgroundColor: '#e6f1fb',
    borderWidth: 1,
    borderColor: '#c5dcef',
  },
  fishDetailActionButtonText: {
    color: '#2c76c7',
    fontSize: 11,
    fontWeight: '900',
  },
  fishDetailDeleteButton: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 10,
    borderRadius: 7,
    backgroundColor: '#fff0f0',
    borderWidth: 1,
    borderColor: '#efcaca',
  },
  fishDetailDeleteButtonText: {
    color: '#b33d3d',
    fontSize: 11,
    fontWeight: '900',
  },
  fishDetailClose: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 12,
    borderRadius: 7,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  fishDetailCloseText: {
    color: '#102421',
    fontSize: 12,
    fontWeight: '900',
  },
  fishDetailHero: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'stretch',
    gap: 20,
    padding: 12,
    borderRadius: 15,
  },
  fishDetailHeroCompact: {
    flexDirection: 'column',
    gap: 12,
  },
  fishDetailImageFrame: {
    flexGrow: 1,
    flexBasis: 310,
    aspectRatio: 1,
    maxWidth: 440,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
    borderRadius: 11,
    backgroundColor: '#ffffff',
  },
  fishDetailImage: {
    width: '92%',
    height: '92%',
  },
  fishDetailCopy: {
    flexGrow: 1,
    flexBasis: 290,
    justifyContent: 'center',
    padding: 8,
  },
  fishDetailKicker: {
    color: '#477466',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  fishDetailTitle: {
    marginTop: 7,
    color: '#102421',
    fontSize: 32,
    lineHeight: 36,
    fontWeight: '900',
  },
  fishDetailDescription: {
    maxWidth: 520,
    marginTop: 12,
    color: '#53645d',
    fontSize: 15,
    lineHeight: 22,
    fontWeight: '600',
  },
  fishDetailTags: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginTop: 18,
  },
  fishTag: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: 7,
    minWidth: 120,
    flexGrow: 1,
    flexBasis: 135,
    paddingVertical: 8,
    borderTopWidth: 1,
    borderTopColor: 'rgba(16, 36, 33, 0.18)',
  },
  fishTagDot: {
    width: 7,
    height: 7,
    marginTop: 4,
    borderRadius: 7,
  },
  fishTagCopy: {
    flex: 1,
  },
  fishTagLabel: {
    color: '#61746e',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  fishTagValue: {
    marginTop: 3,
    color: '#102421',
    fontSize: 12,
    lineHeight: 17,
    fontWeight: '800',
  },
  fishFieldNotes: {
    gap: 0,
    paddingHorizontal: 4,
  },
  fishFieldNotesHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 9,
    paddingBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#dce8df',
  },
  fishFieldNotesBar: {
    width: 4,
    height: 30,
    borderRadius: 4,
  },
  fishFieldNotesEyebrow: {
    color: '#6d7b75',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  fishFieldNotesTitle: {
    marginTop: 3,
    color: '#102421',
    fontSize: 14,
    fontWeight: '900',
  },
  fishInsightRow: {
    minHeight: 54,
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: 10,
    paddingVertical: 11,
    borderBottomWidth: 1,
    borderBottomColor: '#e5eee7',
  },
  fishInsightDot: {
    width: 7,
    height: 7,
    marginTop: 5,
    borderRadius: 7,
  },
  fishInsightLabel: {
    width: 120,
    color: '#6d7b75',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  fishInsightValue: {
    flex: 1,
    color: '#102421',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '700',
  },
  catchDetailView: {
    gap: 14,
  },
  catchDetailViewCompact: {
    gap: 10,
  },
  catchHeroFrame: {
    minHeight: 430,
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
    overflow: 'hidden',
    borderRadius: 16,
  },
  catchHeroFrameCompact: {
    minHeight: 290,
  },
  catchHeroImage: {
    width: '100%',
    height: 430,
  },
  catchHeroShade: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    height: 126,
    backgroundColor: 'rgba(11, 25, 25, 0.54)',
  },
  catchHeroTag: {
    position: 'absolute',
    left: 18,
    top: 18,
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 4,
  },
  catchHeroTagText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.7,
    textTransform: 'uppercase',
  },
  catchHeroClose: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 13,
    borderRadius: 4,
    backgroundColor: 'rgba(255, 255, 255, 0.92)',
  },
  catchHeroActions: {
    position: 'absolute',
    top: 16,
    right: 16,
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'flex-end',
    gap: 7,
  },
  catchHeroActionButton: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 12,
    borderRadius: 4,
    backgroundColor: 'rgba(255, 255, 255, 0.92)',
  },
  catchHeroActionText: {
    color: '#5c463f',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  catchHeroDeleteButton: {
    minHeight: 38,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 12,
    borderRadius: 4,
    backgroundColor: 'rgba(103, 38, 33, 0.88)',
  },
  catchHeroDeleteText: {
    color: '#ffffff',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  catchHeroCloseText: {
    color: '#25252c',
    fontSize: 12,
    fontWeight: '900',
  },
  catchHeroCopy: {
    position: 'absolute',
    left: 22,
    right: 22,
    bottom: 20,
  },
  catchHeroEyebrow: {
    color: '#f6c7b8',
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.9,
    textTransform: 'uppercase',
  },
  catchHeroTitle: {
    marginTop: 5,
    color: '#ffffff',
    fontSize: 32,
    lineHeight: 37,
    fontWeight: '900',
  },
  catchHeroMeta: {
    marginTop: 5,
    color: '#f2eeee',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '700',
  },
  catchStatStrip: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 1,
    overflow: 'hidden',
    borderRadius: 12,
    backgroundColor: '#ffffff',
    borderWidth: 1,
  },
  catchStatBlock: {
    flexGrow: 1,
    flexBasis: 130,
    minHeight: 86,
    justifyContent: 'center',
    paddingHorizontal: 15,
    backgroundColor: '#fffaf7',
  },
  catchStatDot: {
    width: 7,
    height: 7,
    marginBottom: 7,
    borderRadius: 7,
  },
  catchStatLabel: {
    color: '#82736e',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  catchStatValue: {
    marginTop: 4,
    color: '#25252c',
    fontSize: 16,
    lineHeight: 20,
    fontWeight: '900',
  },
  catchContextPanel: {
    gap: 16,
    padding: 17,
    borderRadius: 12,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#e5e1df',
  },
  catchContextHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  catchContextHeaderBar: {
    width: 4,
    height: 34,
    borderRadius: 4,
  },
  catchContextTitle: {
    marginTop: 4,
    color: '#25252c',
    fontSize: 17,
    lineHeight: 22,
    fontWeight: '900',
  },
  catchContextGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  catchContextItem: {
    flexGrow: 1,
    flexBasis: 220,
    minHeight: 64,
    justifyContent: 'center',
    padding: 11,
    borderRadius: 8,
    backgroundColor: '#fafafa',
  },
  catchContextItemWide: {
    flexBasis: '100%',
  },
  catchLureCard: {
    minHeight: 68,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    padding: 9,
    borderRadius: 8,
    backgroundColor: '#fff4ee',
    borderWidth: 1,
    borderColor: '#f0d7ca',
  },
  catchLureImage: {
    width: 74,
    height: 54,
  },
  catchLureCopy: {
    flex: 1,
  },
  catchLureLabel: {
    color: '#a15a48',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  catchLureName: {
    marginTop: 4,
    color: '#4f3731',
    fontSize: 14,
    lineHeight: 18,
    fontWeight: '900',
  },
  catchContextLabel: {
    color: '#82736e',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  catchContextValue: {
    marginTop: 5,
    color: '#3c3b43',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '800',
  },
  catchDetailGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 14,
  },
  catchPhotoPanel: {
    flexGrow: 1,
    flexBasis: 260,
    minHeight: 230,
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
    overflow: 'hidden',
    borderRadius: 12,
  },
  catchPhoto: {
    width: '88%',
    height: 190,
    borderRadius: 12,
  },
  detailPhotoTag: {
    position: 'absolute',
    left: 12,
    bottom: 12,
    paddingHorizontal: 9,
    paddingVertical: 6,
    borderRadius: 999,
  },
  detailPhotoTagText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  catchStatsPanel: {
    flexGrow: 1,
    flexBasis: 300,
    justifyContent: 'center',
    padding: 6,
  },
  detailStatGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 9,
  },
  detailStat: {
    flexGrow: 1,
    flexBasis: 120,
    minHeight: 72,
    justifyContent: 'center',
    padding: 10,
    borderRadius: 9,
    backgroundColor: '#f7faf7',
    borderWidth: 1,
    borderColor: '#e0e8e1',
  },
  detailStatDot: {
    width: 7,
    height: 7,
    marginBottom: 7,
    borderRadius: 7,
  },
  detailStatLabel: {
    color: '#6d7b75',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  detailStatValue: {
    marginTop: 3,
    color: '#102421',
    fontSize: 15,
    lineHeight: 19,
    fontWeight: '900',
  },
  spotMapPanel: {
    minHeight: 220,
    position: 'relative',
    overflow: 'hidden',
    borderRadius: 12,
  },
  spotDetailImage: {
    width: '100%',
    height: 220,
    opacity: 0.84,
  },
  spotCoordinatesBadge: {
    position: 'absolute',
    left: 14,
    bottom: 14,
    paddingHorizontal: 12,
    paddingVertical: 9,
    borderRadius: 9,
    backgroundColor: 'rgba(8, 47, 63, 0.9)',
  },
  spotCoordinatesLabel: {
    color: '#a8e3e0',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  spotCoordinatesValue: {
    marginTop: 3,
    color: '#ffffff',
    fontSize: 15,
    fontWeight: '900',
  },
  planMissionHeader: {
    minHeight: 100,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 14,
    padding: 16,
    borderRadius: 12,
  },
  planDateBadge: {
    maxWidth: 190,
    paddingHorizontal: 12,
    paddingVertical: 9,
    borderRadius: 9,
  },
  planDateText: {
    color: '#ffffff',
    fontSize: 12,
    lineHeight: 17,
    fontWeight: '900',
    textAlign: 'center',
  },
  planSteps: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 9,
  },
  planStep: {
    flexGrow: 1,
    flexBasis: 190,
    minHeight: 84,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    padding: 12,
    borderRadius: 10,
    backgroundColor: '#fffdf7',
    borderWidth: 1,
    borderColor: '#eee3bd',
  },
  planStepNumber: {
    fontSize: 20,
    fontWeight: '900',
  },
  planStepCopy: {
    flex: 1,
  },
  planStepValue: {
    marginTop: 5,
    color: '#102421',
    fontSize: 14,
    lineHeight: 18,
    fontWeight: '900',
  },
  sessionStatusHeader: {
    minHeight: 100,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 16,
    borderRadius: 12,
  },
  sessionStatusOrb: {
    width: 64,
    height: 64,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 64,
  },
  sessionStatusOrbText: {
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '900',
  },
  sessionTimeline: {
    minHeight: 92,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 6,
    paddingHorizontal: 8,
  },
  timelinePoint: {
    flex: 1,
    alignItems: 'center',
    gap: 5,
  },
  timelineDot: {
    width: 13,
    height: 13,
    borderRadius: 13,
  },
  timelineLine: {
    flex: 1,
    height: 2,
    opacity: 0.35,
  },
  timelineValue: {
    color: '#102421',
    fontSize: 13,
    fontWeight: '900',
    textAlign: 'center',
  },
  lureInventoryHero: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    padding: 12,
    borderRadius: 12,
    backgroundColor: '#fff7fa',
  },
  lureInventoryImageFrame: {
    width: 124,
    height: 124,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 11,
  },
  lureInventoryImage: {
    width: 108,
    height: 108,
    borderRadius: 10,
  },
  lureInventoryCopy: {
    flex: 1,
  },
  quantityBlock: {
    width: 78,
    minHeight: 78,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 11,
  },
  quantityValue: {
    color: '#ffffff',
    fontSize: 28,
    lineHeight: 31,
    fontWeight: '900',
  },
  quantityLabel: {
    marginTop: 2,
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  editorialDetailGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    gap: 18,
    padding: 10,
    borderRadius: 12,
    backgroundColor: '#f3fbf6',
  },
  editorialImageFrame: {
    flexGrow: 1,
    flexBasis: 230,
    minHeight: 220,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 11,
  },
  editorialImage: {
    width: '90%',
    height: 190,
    borderRadius: 11,
  },
  editorialCopy: {
    flexGrow: 1,
    flexBasis: 300,
    padding: 8,
  },
  tacticGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  tacticBlock: {
    flexGrow: 1,
    flexBasis: 210,
    minHeight: 100,
    padding: 12,
    borderTopWidth: 4,
    borderRadius: 10,
    backgroundColor: '#f7faf7',
  },
  tacticValue: {
    marginTop: 8,
    color: '#53645d',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '700',
  },
  techniqueDetailGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    gap: 16,
  },
  techniqueImageFrame: {
    flexGrow: 1,
    flexBasis: 230,
    minHeight: 200,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 11,
  },
  techniqueImage: {
    width: '88%',
    height: 170,
    borderRadius: 11,
  },
  techniqueCopy: {
    flexGrow: 1,
    flexBasis: 300,
    padding: 8,
  },
  lureDetailView: {
    gap: 16,
  },
  lureDetailTitle: {
    marginTop: 5,
    color: '#102421',
    fontSize: 30,
    lineHeight: 35,
    fontWeight: '900',
  },
  lureDetailDescription: {
    marginTop: 10,
    color: '#53645d',
    fontSize: 14,
    lineHeight: 21,
    fontWeight: '600',
  },
  lureIdentityFacts: {
    gap: 0,
    marginTop: 14,
    borderTopWidth: 1,
    borderTopColor: '#dce6ef',
  },
  lureActionStudio: {
    gap: 12,
    padding: 14,
    borderRadius: 13,
    backgroundColor: '#f7fbff',
    borderWidth: 1,
    borderColor: '#d7e3f0',
  },
  lureActionHeader: {
    minHeight: 38,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 12,
  },
  lureActionHint: {
    marginTop: 4,
    color: '#647b91',
    fontSize: 12,
    fontWeight: '700',
  },
  lureActionCount: {
    color: '#3978bb',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  lureActionStage: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'stretch',
    gap: 16,
    padding: 10,
    borderRadius: 11,
    backgroundColor: '#ffffff',
    borderWidth: 1,
  },
  lureActionStageCompact: {
    flexDirection: 'column',
  },
  lureActionStageGuide: {
    alignItems: 'center',
    backgroundColor: '#102d40',
  },
  lureActionStageVisual: {
    flexGrow: 1,
    flexBasis: 245,
    aspectRatio: 1,
    maxWidth: 330,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
    borderRadius: 9,
  },
  lureActionStageVisualGuide: {
    width: '100%',
    maxWidth: 720,
    flexBasis: 'auto',
    backgroundColor: '#081c2b',
  },
  lureActionStageImage: {
    width: '88%',
    height: '88%',
  },
  lureActionStageCopy: {
    flexGrow: 1,
    flexBasis: 270,
    justifyContent: 'center',
    padding: 8,
  },
  lureActionStageCopyGuide: {
    maxWidth: 520,
  },
  lureActionStageLabel: {
    color: '#6d7b75',
    fontSize: 10,
    fontWeight: '900',
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  lureActionStageLabelGuide: {
    color: '#9ed5ff',
  },
  lureActionStageTitle: {
    marginTop: 6,
    color: '#102421',
    fontSize: 24,
    lineHeight: 29,
    fontWeight: '900',
  },
  lureActionStageTitleGuide: {
    color: '#ffffff',
  },
  lureActionStageDescription: {
    marginTop: 9,
    color: '#53645d',
    fontSize: 14,
    lineHeight: 21,
    fontWeight: '600',
  },
  lureActionStageDescriptionGuide: {
    color: '#dcecf5',
  },
  lureActionStagePrompt: {
    marginTop: 16,
    fontSize: 11,
    fontWeight: '900',
    letterSpacing: 0.4,
    textTransform: 'uppercase',
  },
  lureActionPicker: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 9,
  },
  lureActionOption: {
    flexGrow: 1,
    flexBasis: 135,
    maxWidth: 190,
    minHeight: 112,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 6,
    padding: 8,
    borderRadius: 9,
    backgroundColor: '#eef5fc',
    borderWidth: 1,
    borderColor: '#d7e3f0',
  },
  lureActionOptionIcon: {
    width: 66,
    height: 66,
  },
  lureActionOptionText: {
    color: '#2b5c8e',
    fontSize: 11,
    fontWeight: '900',
    textAlign: 'center',
  },
  lureInfoRail: {
    gap: 0,
    paddingHorizontal: 4,
  },
  lureInfoRailHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingBottom: 9,
    borderBottomWidth: 1,
    borderBottomColor: '#dce6ef',
  },
  lureInfoRailBar: {
    width: 4,
    height: 22,
    borderRadius: 4,
  },
  lureInfoRailTitle: {
    color: '#102421',
    fontSize: 13,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  lureInfoLine: {
    minHeight: 48,
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: 14,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#e3ebf2',
  },
  lureInfoLabel: {
    width: 122,
    color: '#6d7b75',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  lureInfoValue: {
    flex: 1,
    color: '#102421',
    fontSize: 13,
    lineHeight: 19,
    fontWeight: '700',
  },
  techniqueScoreRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  scoreBlock: {
    flexGrow: 1,
    flexBasis: 240,
    gap: 8,
    padding: 12,
    borderRadius: 10,
    backgroundColor: '#f4f8ff',
  },
  scoreHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 10,
  },
  scoreValue: {
    fontSize: 12,
    fontWeight: '900',
  },
  scoreTrack: {
    height: 8,
    overflow: 'hidden',
    borderRadius: 8,
    backgroundColor: '#dce6f2',
  },
  scoreFill: {
    height: 8,
    borderRadius: 8,
  },
  closeButton: {
    minHeight: 40,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 13,
    borderRadius: 8,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  closeButtonText: {
    color: '#102421',
    fontSize: 13,
    fontWeight: '900',
  },
  detailGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  detailRow: {
    flexGrow: 1,
    flexBasis: 210,
    minHeight: 72,
    justifyContent: 'center',
    padding: 12,
    borderRadius: 8,
    backgroundColor: '#edf3ef',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  detailLabel: {
    color: '#6d7b75',
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  detailValue: {
    marginTop: 6,
    color: '#102421',
    fontSize: 14,
    lineHeight: 19,
    fontWeight: '800',
  },
  resourceGroup: {
    gap: 10,
  },
  resourceGroupAtlas: {
    gap: 13,
  },
  resourceGroupMission: {
    gap: 14,
    padding: 14,
    borderRadius: 18,
    backgroundColor: '#f8f6fc',
    borderWidth: 1,
    borderColor: '#e1d9ee',
  },
  resourceGroupLive: {
    gap: 13,
    padding: 14,
    borderRadius: 18,
    backgroundColor: '#f0faf7',
    borderWidth: 1,
    borderColor: '#c9e7dc',
  },
  resourceGroupGear: {
    gap: 13,
  },
  resourceGroupHeader: {
    minHeight: 44,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    gap: 10,
  },
  resourceGroupHeaderAtlas: {
    paddingHorizontal: 4,
  },
  resourceGroupHeaderMission: {
    paddingHorizontal: 2,
  },
  resourceGroupHeaderLive: {
    paddingHorizontal: 2,
  },
  resourceGroupHeaderGear: {
    paddingHorizontal: 4,
  },
  resourceGroupHeading: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  groupMarker: {
    width: 5,
    height: 38,
    borderRadius: 6,
  },
  resourceCount: {
    marginTop: 3,
    color: '#53645d',
    fontSize: 14,
    fontWeight: '800',
  },
  paginationControls: {
    minHeight: 38,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  pageButton: {
    minHeight: 36,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 11,
    borderRadius: 8,
    backgroundColor: '#dff8f3',
    borderWidth: 1,
    borderColor: '#b8e9df',
  },
  pageButtonDisabled: {
    backgroundColor: '#edf3ef',
    borderColor: '#d6ded7',
  },
  pageButtonText: {
    color: '#082f3f',
    fontSize: 12,
    fontWeight: '900',
  },
  pageButtonTextDisabled: {
    color: '#8a9992',
  },
  pageIndicator: {
    color: '#53645d',
    fontSize: 13,
    fontWeight: '900',
  },
  resourceGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  resourceCard: {
    flexGrow: 1,
    flexBasis: 280,
    maxWidth: 460,
    minHeight: 146,
    flexDirection: 'row',
    gap: 12,
    padding: 10,
    borderRadius: 12,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  resourceCardSpot: {
    minHeight: 176,
    borderRadius: 16,
    borderLeftWidth: 5,
    backgroundColor: '#f8fcf8',
  },
  resourceCardVisual: {
    flexDirection: 'column',
    flexBasis: 220,
    maxWidth: 300,
    minHeight: 258,
    padding: 12,
  },
  resourceCardPlan: {
    minHeight: 178,
    borderRadius: 16,
    backgroundColor: '#ffffff',
    borderTopWidth: 4,
  },
  resourceCardSession: {
    minHeight: 178,
    borderRadius: 16,
    backgroundColor: '#f8fffc',
    borderRightWidth: 5,
  },
  resourceCardInventory: {
    minHeight: 178,
    borderRadius: 16,
    backgroundColor: '#fff8fb',
    borderBottomWidth: 4,
  },
  resourceImageFrame: {
    width: 94,
    minHeight: 112,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 10,
    overflow: 'hidden',
  },
  resourceImageFrameVisual: {
    width: '100%',
    minHeight: 140,
    height: 140,
  },
  resourceImage: {
    width: 88,
    height: 88,
    borderRadius: 10,
  },
  resourceImageVisual: {
    width: '88%',
    height: 122,
  },
  resourceCardBody: {
    flex: 1,
    minWidth: 0,
  },
  resourceTitle: {
    color: '#102421',
    fontSize: 17,
    lineHeight: 22,
    fontWeight: '900',
  },
  resourceMeta: {
    marginTop: 5,
    color: '#27615d',
    fontSize: 13,
    lineHeight: 18,
    fontWeight: '800',
  },
  resourceDetail: {
    marginTop: 5,
    color: '#5a6b63',
    fontSize: 13,
    lineHeight: 18,
    fontWeight: '600',
  },
  resourceCardFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 8,
    marginTop: 10,
  },
  resourceCardAction: {
    fontSize: 11,
    fontWeight: '900',
    textTransform: 'uppercase',
  },
  resourceBadge: {
    paddingHorizontal: 8,
    paddingVertical: 5,
    borderRadius: 999,
    backgroundColor: '#dff8f3',
    borderWidth: 1,
    borderColor: '#b8e9df',
  },
  resourceBadgeText: {
    color: '#082f3f',
    fontSize: 11,
    fontWeight: '900',
  },
  emptyPanel: {
    minHeight: 88,
    justifyContent: 'center',
    padding: 16,
    borderRadius: 8,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#d6ded7',
  },
  emptyText: {
    color: '#5a6b63',
    fontSize: 14,
    fontWeight: '800',
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
