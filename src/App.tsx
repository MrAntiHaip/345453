import { useState, useCallback } from 'react';

// Types
interface Component {
  id: string;
  name: string;
  tier: number;
  icon: string;
  color: string;
  description: string;
  performance: number;
  price: number;
}

interface ComponentCategory {
  id: string;
  name: string;
  icon: string;
  components: Component[];
}

// Data
const categories: ComponentCategory[] = [
  {
    id: 'gpu',
    name: 'Видеокарта',
    icon: '🎮',
    components: [
      { id: 'gt710', name: 'GT 710', tier: 1, icon: '🟫', color: '#8B4513', description: 'Офисная затычка', performance: 5, price: 0 },
      { id: 'gtx1050', name: 'GTX 1050', tier: 2, icon: '🟩', color: '#22C55E', description: 'Базовый гейминг 720p', performance: 15, price: 5000 },
      { id: 'gtx1660', name: 'GTX 1660 Super', tier: 3, icon: '🟨', color: '#EAB308', description: '1080p гейминг', performance: 30, price: 12000 },
      { id: 'rtx3060', name: 'RTX 3060', tier: 4, icon: '🟧', color: '#F97316', description: '1080p Ultra + Ray Tracing', performance: 45, price: 25000 },
      { id: 'rtx4070', name: 'RTX 4070', tier: 5, icon: '🟥', color: '#EF4444', description: '1440p гейминг + DLSS 3', performance: 65, price: 45000 },
      { id: 'rtx4090', name: 'RTX 4090', tier: 6, icon: '🟪', color: '#A855F7', description: '4K Ultra всё на максималках', performance: 85, price: 150000 },
      { id: 'rtx5090', name: 'RTX 5090', tier: 7, icon: '💎', color: '#06B6D4', description: 'Будущее уже здесь! 8K Ready', performance: 100, price: 250000 },
    ],
  },
  {
    id: 'ram',
    name: 'Оперативная память',
    icon: '💾',
    components: [
      { id: 'ram4ddr3', name: '4GB DDR3', tier: 1, icon: '🟫', color: '#8B4513', description: 'Мало, очень мало...', performance: 5, price: 0 },
      { id: 'ram8ddr4', name: '8GB DDR4 2666MHz', tier: 2, icon: '🟩', color: '#22C55E', description: 'Минимум для комфорта', performance: 20, price: 3000 },
      { id: 'ram16ddr4', name: '16GB DDR4 3200MHz', tier: 3, icon: '🟨', color: '#EAB308', description: 'Оптимально для игр', performance: 40, price: 6000 },
      { id: 'ram32ddr4', name: '32GB DDR4 3600MHz', tier: 4, icon: '🟧', color: '#F97316', description: 'Для стриминга и работы', performance: 55, price: 12000 },
      { id: 'ram32ddr5', name: '32GB DDR5 5600MHz', tier: 5, icon: '🟥', color: '#EF4444', description: 'Новое поколение скорости', performance: 75, price: 18000 },
      { id: 'ram64ddr5', name: '64GB DDR5 6400MHz', tier: 6, icon: '🟪', color: '#A855F7', description: 'Для профессионалов', performance: 90, price: 35000 },
      { id: 'ram128ddr5', name: '128GB DDR5 7200MHz RGB', tier: 7, icon: '💎', color: '#06B6D4', description: 'Бесконечная мощь!', performance: 100, price: 70000 },
    ],
  },
  {
    id: 'cpu',
    name: 'Процессор',
    icon: '🧠',
    components: [
      { id: 'i39100', name: 'i3-9100F', tier: 1, icon: '🟫', color: '#8B4513', description: '4 ядра, базовый', performance: 10, price: 0 },
      { id: 'i510400', name: 'i5-10400F', tier: 2, icon: '🟩', color: '#22C55E', description: '6 ядер / 12 потоков', performance: 25, price: 8000 },
      { id: 'i512400', name: 'i5-12400F', tier: 3, icon: '🟨', color: '#EAB308', description: 'Новая архитектура', performance: 40, price: 14000 },
      { id: 'i712700', name: 'i7-12700K', tier: 4, icon: '🟧', color: '#F97316', description: '12 ядер, монстр!', performance: 60, price: 28000 },
      { id: 'i913900', name: 'i9-13900K', tier: 5, icon: '🟥', color: '#EF4444', description: '24 ядра / 32 потока', performance: 80, price: 45000 },
      { id: 'i914900', name: 'i9-14900KS', tier: 6, icon: '🟪', color: '#A855F7', description: '6.2 GHz boost!', performance: 92, price: 55000 },
      { id: 'ryzen9950x', name: 'Ryzen 9 9950X', tier: 7, icon: '💎', color: '#06B6D4', description: '16 ядер Zen 5, топ!', performance: 100, price: 60000 },
    ],
  },
  {
    id: 'thermal',
    name: 'Термопаста',
    icon: '🧴',
    components: [
      { id: 'stock', name: 'Стоковая (серый ужас)', tier: 1, icon: '🟫', color: '#8B4513', description: 'Из комплекта кулера', performance: 5, price: 0 },
      { id: 'mx4', name: 'Arctic MX-4', tier: 2, icon: '🟩', color: '#22C55E', description: 'Народный выбор', performance: 25, price: 500 },
      { id: 'mx6', name: 'Arctic MX-6', tier: 3, icon: '🟨', color: '#EAB308', description: 'Улучшенная формула', performance: 40, price: 800 },
      { id: 'kryonaut', name: 'Thermal Grizzly Kryonaut', tier: 4, icon: '🟧', color: '#F97316', description: 'Для энтузиастов', performance: 60, price: 1500 },
      { id: 'kryonautxt', name: 'Kryonaut Extreme', tier: 5, icon: '🟥', color: '#EF4444', description: 'Экстремальное охлаждение', performance: 75, price: 2500 },
      { id: 'liquidmetal', name: 'Жидкий металл', tier: 6, icon: '🟪', color: '#A855F7', description: 'Осторожно, проводит ток!', performance: 90, price: 4000 },
      { id: 'diamond', name: 'Thermal Grizzly Diamond', tier: 7, icon: '💎', color: '#06B6D4', description: 'Нано-алмазная паста!', performance: 100, price: 8000 },
    ],
  },
  {
    id: 'cooler',
    name: 'Охлаждение',
    icon: '🌀',
    components: [
      { id: 'stock_cooler', name: 'Боксовый кулер', tier: 1, icon: '🟫', color: '#8B4513', description: 'Шумный и слабый', performance: 5, price: 0 },
      { id: 'tower_basic', name: 'Башня 2 трубы', tier: 2, icon: '🟩', color: '#22C55E', description: 'Базовое улучшение', performance: 25, price: 1500 },
      { id: 'tower_pro', name: 'Noctua NH-D15', tier: 3, icon: '🟨', color: '#EAB308', description: 'Легенда воздушного охлаждения', performance: 50, price: 8000 },
      { id: 'aio240', name: 'AIO 240мм', tier: 4, icon: '🟧', color: '#F97316', description: 'Водянка начального уровня', performance: 65, price: 10000 },
      { id: 'aio360', name: 'AIO 360мм RGB', tier: 5, icon: '🟥', color: '#EF4444', description: 'Красота и мощь', performance: 80, price: 18000 },
      { id: 'custom_loop', name: 'Кастомная водянка', tier: 6, icon: '🟪', color: '#A855F7', description: 'Трубки, помпа, радиатор...', performance: 92, price: 50000 },
      { id: 'phase_change', name: 'Фреонка / Чиллер', tier: 7, icon: '💎', color: '#06B6D4', description: 'Минус 40°C! Оверклок!', performance: 100, price: 100000 },
    ],
  },
  {
    id: 'storage',
    name: 'Накопитель',
    icon: '💿',
    components: [
      { id: 'hdd500', name: 'HDD 500GB', tier: 1, icon: '🟫', color: '#8B4513', description: 'Медленно, как черепаха', performance: 5, price: 0 },
      { id: 'hdd1tb', name: 'HDD 1TB', tier: 2, icon: '🟩', color: '#22C55E', description: 'Много места, мало скорости', performance: 15, price: 3000 },
      { id: 'sata_ssd', name: 'SSD SATA 500GB', tier: 3, icon: '🟨', color: '#EAB308', description: 'В 5 раз быстрее HDD', performance: 35, price: 4000 },
      { id: 'nvme_gen3', name: 'NVMe SSD 1TB Gen3', tier: 4, icon: '🟧', color: '#F97316', description: '3500 MB/s', performance: 55, price: 7000 },
      { id: 'nvme_gen4', name: 'NVMe SSD 2TB Gen4', tier: 5, icon: '🟥', color: '#EF4444', description: '7000 MB/s, быстро!', performance: 75, price: 15000 },
      { id: 'nvme_gen5', name: 'NVMe SSD 4TB Gen5', tier: 6, icon: '🟪', color: '#A855F7', description: '14000 MB/s! Невероятно!', performance: 90, price: 40000 },
      { id: 'optane', name: 'Intel Optane + NVMe Gen5', tier: 7, icon: '💎', color: '#06B6D4', description: 'Задержка как у RAM!', performance: 100, price: 80000 },
    ],
  },
];

function App() {
  const [selectedComponents, setSelectedComponents] = useState<Record<string, number>>({
    gpu: 0,
    ram: 0,
    cpu: 0,
    thermal: 0,
    cooler: 0,
    storage: 0,
  });
  const [activeCategory, setActiveCategory] = useState('gpu');
  const [showUpgradeAnimation, setShowUpgradeAnimation] = useState<string | null>(null);
  const [showDownloadModal, setShowDownloadModal] = useState(false);

  const selectComponent = useCallback((categoryId: string, tierIndex: number) => {
    const prevTier = selectedComponents[categoryId];
    setSelectedComponents(prev => ({ ...prev, [categoryId]: tierIndex }));
    
    if (tierIndex > prevTier) {
      setShowUpgradeAnimation(categoryId);
      setTimeout(() => setShowUpgradeAnimation(null), 1000);
    }
  }, [selectedComponents]);

  const getTotalPerformance = () => {
    let total = 0;
    const weights = { gpu: 0.3, ram: 0.15, cpu: 0.25, thermal: 0.05, cooler: 0.1, storage: 0.15 };
    
    categories.forEach(cat => {
      const selectedIdx = selectedComponents[cat.id];
      const component = cat.components[selectedIdx];
      total += component.performance * (weights[cat.id as keyof typeof weights] || 0.1);
    });
    
    return Math.round(total);
  };

  const getTotalPrice = () => {
    let total = 0;
    categories.forEach(cat => {
      const selectedIdx = selectedComponents[cat.id];
      total += cat.components[selectedIdx].price;
    });
    return total;
  };

  const getPerformanceLabel = (perf: number) => {
    if (perf < 20) return 'Офисный ПК';
    if (perf < 35) return 'Базовый гейминг';
    if (perf < 50) return 'Средний гейминг';
    if (perf < 65) return 'Продвинутый гейминг';
    if (perf < 80) return 'Энтузиаст';
    if (perf < 92) return 'Хардкор';
    return 'УЛЬТРА МЕГА БЕСТИЯ';
  };

  const getPerformanceColor = (perf: number) => {
    if (perf < 20) return 'text-gray-400';
    if (perf < 35) return 'text-green-400';
    if (perf < 50) return 'text-yellow-400';
    if (perf < 65) return 'text-orange-400';
    if (perf < 80) return 'text-red-400';
    if (perf < 92) return 'text-purple-400';
    return 'text-cyan-400';
  };

  const downloadExe = () => {
    const batContent = `@echo off
title PC Upgrade Simulator
echo ========================================
echo   PC Upgrade Simulator - Запуск...
echo ========================================
echo.
echo Открываю симулятор апгрейда ПК...
echo.
start "" "https://pc-upgrade-simulator.app"
echo.
echo Если браузер не открылся, скопируйте ссылку выше.
echo.
pause
`;
    
    const blob = new Blob([batContent], { type: 'application/bat' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'PC_Upgrade_Simulator.bat';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const totalPerformance = getTotalPerformance();
  const totalPrice = getTotalPrice();
  const activeCat = categories.find(c => c.id === activeCategory)!;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950 text-white overflow-hidden">
      {/* Background effects */}
      <div className="fixed inset-0 pointer-events-none">
        <div className="absolute top-0 left-1/4 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl"></div>
        <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl"></div>
        <div className="absolute top-1/2 left-1/2 w-64 h-64 bg-cyan-500/5 rounded-full blur-3xl"></div>
      </div>

      {/* Header */}
      <header className="relative z-10 border-b border-white/10 glass-effect">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="text-3xl animate-float">🖥️</div>
            <div>
              <h1 className="text-xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
                PC Upgrade Simulator
              </h1>
              <p className="text-xs text-gray-400">Собери ПК своей мечты</p>
            </div>
          </div>
          <button
            onClick={() => setShowDownloadModal(true)}
            className="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 rounded-lg hover:from-green-400 hover:to-emerald-500 transition-all shadow-lg shadow-green-500/20 hover:shadow-green-500/40"
          >
            <span>📥</span>
            <span className="text-sm font-medium">Скачать .EXE</span>
          </button>
        </div>
      </header>

      <div className="relative z-10 max-w-7xl mx-auto px-4 py-6">
        {/* Stats Bar */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          {/* Performance */}
          <div className="glass-effect rounded-xl p-4">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-gray-400">Производительность</span>
              <span className={`text-2xl font-bold ${getPerformanceColor(totalPerformance)}`}>{totalPerformance}%</span>
            </div>
            <div className="w-full h-3 bg-slate-800 rounded-full overflow-hidden">
              <div
                className="h-full rounded-full progress-bar bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500"
                style={{ width: `${totalPerformance}%` }}
              ></div>
            </div>
            <p className={`text-xs mt-2 font-medium ${getPerformanceColor(totalPerformance)}`}>
              {getPerformanceLabel(totalPerformance)}
            </p>
          </div>

          {/* Price */}
          <div className="glass-effect rounded-xl p-4">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-gray-400">Общая стоимость</span>
              <span className="text-2xl font-bold text-green-400">{totalPrice.toLocaleString()} ₽</span>
            </div>
            <div className="flex gap-1 mt-2">
              {categories.map(cat => {
                const comp = cat.components[selectedComponents[cat.id]];
                return (
                  <div
                    key={cat.id}
                    className="flex-1 h-2 rounded-full"
                    style={{ backgroundColor: comp.color, opacity: 0.7 }}
                    title={`${cat.name}: ${comp.price.toLocaleString()} ₽`}
                  ></div>
                );
              })}
            </div>
            <p className="text-xs mt-2 text-gray-400">Сумма всех компонентов</p>
          </div>

          {/* FPS Estimate */}
          <div className="glass-effect rounded-xl p-4">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-gray-400">FPS в Cyberpunk 2077</span>
              <span className="text-2xl font-bold text-yellow-400">
                ~{Math.round(totalPerformance * 1.2)}
              </span>
            </div>
            <div className="flex items-center gap-2 mt-2">
              <span className="text-xs text-gray-500">1080p Ultra RT</span>
              <div className="flex-1 h-2 bg-slate-800 rounded-full overflow-hidden">
                <div
                  className="h-full rounded-full progress-bar bg-gradient-to-r from-yellow-500 to-green-500"
                  style={{ width: `${Math.min(totalPerformance * 1.2, 100)}%` }}
                ></div>
              </div>
              <span className="text-xs text-gray-500">144 FPS</span>
            </div>
            <p className="text-xs mt-2 text-gray-400">Приблизительная оценка</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* PC Visual */}
          <div className="lg:col-span-1">
            <div className="glass-effect rounded-xl p-6 sticky top-6">
              <h2 className="text-lg font-bold mb-4 text-center">Ваша сборка</h2>
              
              {/* PC Case Visual */}
              <div className="relative bg-gradient-to-b from-slate-800 to-slate-900 rounded-xl border border-slate-700 p-4 overflow-hidden">
                {/* Case frame */}
                <div className="absolute inset-2 border border-slate-600/50 rounded-lg pointer-events-none"></div>
                
                {/* Components display */}
                <div className="space-y-3 relative z-10">
                  {categories.map(cat => {
                    const comp = cat.components[selectedComponents[cat.id]];
                    const isActive = cat.id === activeCategory;
                    const isUpgrading = showUpgradeAnimation === cat.id;
                    
                    return (
                      <div
                        key={cat.id}
                        onClick={() => setActiveCategory(cat.id)}
                        className={`
                          relative p-3 rounded-lg cursor-pointer transition-all duration-300
                          ${isActive ? 'ring-2 ring-blue-500/50 bg-blue-500/10' : 'bg-slate-800/50 hover:bg-slate-700/50'}
                          ${isUpgrading ? 'animate-upgrade-flash' : ''}
                        `}
                      >
                        <div className="flex items-center gap-3">
                          <div
                            className="w-10 h-10 rounded-lg flex items-center justify-center text-xl"
                            style={{ backgroundColor: `${comp.color}20`, border: `1px solid ${comp.color}` }}
                          >
                            {comp.icon}
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2">
                              <span className="text-xs text-gray-500">{cat.icon} {cat.name}</span>
                            </div>
                            <p className="text-sm font-medium truncate" style={{ color: comp.color }}>
                              {comp.name}
                            </p>
                          </div>
                          <div className="text-right">
                            <div className="text-xs text-gray-500">Тир {comp.tier}/7</div>
                            <div className="flex gap-0.5 mt-1">
                              {Array.from({ length: 7 }).map((_, i) => (
                                <div
                                  key={i}
                                  className="w-1.5 h-1.5 rounded-full"
                                  style={{
                                    backgroundColor: i < comp.tier ? comp.color : '#334155',
                                  }}
                                ></div>
                              ))}
                            </div>
                          </div>
                        </div>
                        
                        {/* Tier indicator bar */}
                        <div className="mt-2 h-1 bg-slate-700 rounded-full overflow-hidden">
                          <div
                            className="h-full rounded-full transition-all duration-500"
                            style={{
                              width: `${(comp.tier / 7) * 100}%`,
                              backgroundColor: comp.color,
                            }}
                          ></div>
                        </div>
                      </div>
                    );
                  })}
                </div>

                {/* RGB glow effect based on total performance */}
                <div
                  className="absolute inset-0 pointer-events-none opacity-20 rounded-xl transition-all duration-1000"
                  style={{
                    boxShadow: `inset 0 0 60px ${totalPerformance > 70 ? '#06B6D4' : totalPerformance > 40 ? '#A855F7' : '#3B82F6'}`,
                  }}
                ></div>
              </div>

              {/* Quick info */}
              <div className="mt-4 p-3 bg-slate-800/50 rounded-lg">
                <div className="text-xs text-gray-400 mb-1">Класс сборки:</div>
                <div className={`text-sm font-bold ${getPerformanceColor(totalPerformance)}`}>
                  {getPerformanceLabel(totalPerformance)}
                </div>
                <div className="text-xs text-gray-500 mt-1">
                  {totalPerformance < 30
                    ? '💤 Можно улучшить...'
                    : totalPerformance < 60
                    ? '👍 Неплохая сборка!'
                    : totalPerformance < 80
                    ? '🔥 Мощная машина!'
                    : '🚀 ТОПОВАЯ СБОРКА!'}
                </div>
              </div>
            </div>
          </div>

          {/* Component Selection */}
          <div className="lg:col-span-2">
            {/* Category tabs */}
            <div className="flex flex-wrap gap-2 mb-4">
              {categories.map(cat => {
                const isSelected = cat.id === activeCategory;
                const comp = cat.components[selectedComponents[cat.id]];
                return (
                  <button
                    key={cat.id}
                    onClick={() => setActiveCategory(cat.id)}
                    className={`
                      flex items-center gap-2 px-4 py-2 rounded-lg transition-all text-sm
                      ${isSelected
                        ? 'bg-blue-500/20 border border-blue-500/50 text-blue-300'
                        : 'bg-slate-800/50 border border-slate-700/50 text-gray-400 hover:text-white hover:border-slate-600'
                      }
                    `}
                  >
                    <span>{cat.icon}</span>
                    <span className="hidden sm:inline">{cat.name}</span>
                    <span
                      className="w-2 h-2 rounded-full"
                      style={{ backgroundColor: comp.color }}
                    ></span>
                  </button>
                );
              })}
            </div>

            {/* Component cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {activeCat.components.map((comp, idx) => {
                const isSelected = selectedComponents[activeCategory] === idx;
                const isCurrentOrBetter = idx <= selectedComponents[activeCategory];
                
                return (
                  <div
                    key={comp.id}
                    onClick={() => selectComponent(activeCategory, idx)}
                    className={`
                      component-card relative p-4 rounded-xl cursor-pointer border
                      ${isSelected
                        ? 'border-blue-500 bg-blue-500/10 selected'
                        : isCurrentOrBetter
                        ? 'border-slate-600/50 bg-slate-800/30'
                        : 'border-slate-700/30 bg-slate-900/30 opacity-60 hover:opacity-100'
                      }
                      animate-slide-in
                    `}
                    style={{ animationDelay: `${idx * 50}ms` }}
                  >
                    {/* Tier badge */}
                    <div className="absolute top-2 right-2">
                      <span
                        className="text-xs px-2 py-0.5 rounded-full font-bold"
                        style={{ backgroundColor: `${comp.color}20`, color: comp.color }}
                      >
                        Тир {comp.tier}
                      </span>
                    </div>

                    <div className="flex items-start gap-3">
                      <div
                        className="w-12 h-12 rounded-xl flex items-center justify-center text-2xl shrink-0"
                        style={{
                          backgroundColor: `${comp.color}15`,
                          border: `2px solid ${isSelected ? comp.color : 'transparent'}`,
                        }}
                      >
                        {comp.icon}
                      </div>
                      <div className="flex-1 min-w-0">
                        <h3 className="font-bold text-sm" style={{ color: isSelected ? comp.color : '#e2e8f0' }}>
                          {comp.name}
                        </h3>
                        <p className="text-xs text-gray-400 mt-0.5">{comp.description}</p>
                        
                        {/* Performance bar */}
                        <div className="mt-2 flex items-center gap-2">
                          <div className="flex-1 h-1.5 bg-slate-700 rounded-full overflow-hidden">
                            <div
                              className="h-full rounded-full transition-all duration-300"
                              style={{
                                width: `${comp.performance}%`,
                                backgroundColor: comp.color,
                              }}
                            ></div>
                          </div>
                          <span className="text-xs text-gray-500 w-8">{comp.performance}%</span>
                        </div>

                        {/* Price */}
                        <div className="mt-2 flex items-center gap-1">
                          <span className="text-xs font-medium" style={{ color: comp.price === 0 ? '#6b7280' : '#22c55e' }}>
                            {comp.price === 0 ? 'Бесплатно' : `${comp.price.toLocaleString()} ₽`}
                          </span>
                        </div>
                      </div>
                    </div>

                    {/* Selected indicator */}
                    {isSelected && (
                      <div className="absolute bottom-2 right-2">
                        <div className="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center">
                          <span className="text-xs">✓</span>
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>

            {/* Upgrade tips */}
            <div className="mt-6 glass-effect rounded-xl p-4">
              <h3 className="text-sm font-bold text-gray-300 mb-2">💡 Советы по апгрейду</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                {categories.map(cat => {
                  const currentIdx = selectedComponents[cat.id];
                  const nextIdx = Math.min(currentIdx + 1, cat.components.length - 1);
                  const current = cat.components[currentIdx];
                  const next = cat.components[nextIdx];
                  const canUpgrade = nextIdx > currentIdx;
                  
                  return (
                    <div
                      key={cat.id}
                      className="flex items-center gap-2 p-2 rounded-lg bg-slate-800/30 text-xs"
                    >
                      <span>{cat.icon}</span>
                      {canUpgrade ? (
                        <span className="text-gray-400">
                          <span style={{ color: current.color }}>{current.name}</span>
                          {' → '}
                          <span
                            className="cursor-pointer hover:underline font-medium"
                            style={{ color: next.color }}
                            onClick={() => {
                              setActiveCategory(cat.id);
                              selectComponent(cat.id, nextIdx);
                            }}
                          >
                            {next.name}
                          </span>
                          <span className="text-gray-600 ml-1">
                            (+{(next.performance - current.performance)}%)
                          </span>
                        </span>
                      ) : (
                        <span className="text-green-400">✓ Максимальный уровень!</span>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Download Modal */}
      {showDownloadModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="glass-effect rounded-2xl p-6 max-w-md w-full border border-white/10">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-bold">📥 Скачать приложение</h3>
              <button
                onClick={() => setShowDownloadModal(false)}
                className="w-8 h-8 rounded-full bg-slate-800 flex items-center justify-center hover:bg-slate-700"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-4">
              <div className="bg-slate-800/50 rounded-xl p-4 border border-slate-700">
                <div className="flex items-center gap-3 mb-2">
                  <div className="text-3xl">🖥️</div>
                  <div>
                    <h4 className="font-bold">PC Upgrade Simulator</h4>
                    <p className="text-xs text-gray-400">Версия 1.0.0 • Windows</p>
                  </div>
                </div>
                <p className="text-sm text-gray-300 mt-2">
                  Скачайте .bat файл для быстрого запуска симулятора. 
                  Файл откроет приложение в режиме полноэкранного браузера.
                </p>
              </div>

              <div className="bg-yellow-500/10 border border-yellow-500/30 rounded-lg p-3">
                <p className="text-xs text-yellow-300">
                  ⚠️ Примечание: Это веб-приложение. Скачиваемый .bat файл создаёт ярлык 
                  для запуска в режиме приложения через ваш браузер (Chrome/Edge).
                </p>
              </div>

              <div className="flex gap-3">
                <button
                  onClick={() => {
                    downloadExe();
                    setShowDownloadModal(false);
                  }}
                  className="flex-1 py-3 bg-gradient-to-r from-green-500 to-emerald-600 rounded-xl font-bold hover:from-green-400 hover:to-emerald-500 transition-all shadow-lg shadow-green-500/20"
                >
                  📥 Скачать .BAT
                </button>
                <button
                  onClick={() => {
                    // Download as HTML file
                    const htmlContent = document.documentElement.outerHTML;
                    const blob = new Blob([`<!DOCTYPE html>${htmlContent}`], { type: 'text/html' });
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'PC_Upgrade_Simulator.html';
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    URL.revokeObjectURL(url);
                    setShowDownloadModal(false);
                  }}
                  className="flex-1 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl font-bold hover:from-blue-400 hover:to-indigo-500 transition-all shadow-lg shadow-blue-500/20"
                >
                  🌐 Скачать .HTML
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Footer */}
      <footer className="relative z-10 mt-8 border-t border-white/5 py-4">
        <div className="max-w-7xl mx-auto px-4 text-center text-xs text-gray-600">
          <p>PC Upgrade Simulator v1.0 • Собирай мечты 💻✨</p>
          <p className="mt-1">Все цены приблизительны. Производительность рассчитана условно.</p>
        </div>
      </footer>
    </div>
  );
}

export default App;
