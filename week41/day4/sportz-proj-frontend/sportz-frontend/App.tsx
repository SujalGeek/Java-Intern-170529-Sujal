import React, { useEffect, useMemo, useState } from 'react';
import { useMatchData } from './hooks/useMatchData';
import { MatchCard } from './components/MatchCard';
import { LiveFeed } from './components/LiveFeed';
import { StatusIndicator } from './components/StatusIndicator';
import { API_BASE_URL, WS_BASE_URL } from './constants';

const App: React.FC = () => {
  const pageSize = 6;
  const [currentPage, setCurrentPage] = useState(1);
  const {
    matches,
    isLoading,
    error,
    commentary,
    isCommentaryLoading,
    wsError,
    status,
    activeMatchId,
    newMatchesCount,
    dismissNewMatches,
    watchMatch,
    unwatchMatch,
    reloadMatches,
  } = useMatchData();

  const totalPages = Math.max(1, Math.ceil(matches.length / pageSize));

  useEffect(() => {
    if (currentPage > totalPages) {
      setCurrentPage(totalPages);
    }
  }, [currentPage, totalPages]);

  const pagedMatches = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return matches.slice(startIndex, startIndex + pageSize);
  }, [matches, currentPage, pageSize]);

  return (
    <div className="min-h-screen p-4 md:p-8 font-sans">
      <div className="max-w-7xl mx-auto space-y-8">
        
        {/* Header Section */}
        <header className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-brand-yellow border-2 border-black rounded-2xl p-6 shadow-hard">
          <div>
            <h1 className="text-3xl font-black tracking-tight text-brand-dark mb-1">
              Spotrz
            </h1>
            <p className="text-sm font-medium opacity-80">Real-time match data demo</p>
          </div>
          <div className="flex flex-col items-end gap-2">
            <StatusIndicator status={status} />
            {wsError && (
              <span className="text-xs font-mono bg-red-100 text-red-700 border border-red-200 px-2 py-1 rounded">
                WS: {wsError}
              </span>
            )}
          </div>
        </header>

        {/* Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          {/* Left Column: Match List */}
          <main className="lg:col-span-2 space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-bold border-l-4 border-brand-blue pl-3">Current Matches</h2>
              <span className="text-xs font-mono bg-black text-white px-2 py-1 rounded">
                API: {isLoading ? '...' : matches.length}
              </span>
            </div>
            {newMatchesCount > 0 && (
              <div className="flex items-center justify-between gap-3 bg-brand-yellow border-2 border-black rounded-xl px-4 py-3 shadow-hard-sm">
                <span className="text-sm font-bold">
                  {newMatchesCount} new match{newMatchesCount > 1 ? 'es' : ''} added
                </span>
                <button
                  onClick={dismissNewMatches}
                  className="px-3 py-1 rounded-full text-xs font-bold border-2 border-black bg-white hover:bg-gray-50 transition-all"
                >
                  Dismiss
                </button>
              </div>
            )}

            {isLoading && (
              <div className="p-12 text-center border-2 border-dashed border-gray-300 rounded-2xl">
                <div className="animate-spin w-8 h-8 border-4 border-brand-yellow border-t-black rounded-full mx-auto mb-4"></div>
                <p className="font-medium text-gray-500">Loading matches...</p>
              </div>
            )}

            {error && (
               <div className="bg-red-50 border-2 border-red-500 text-red-900 p-6 rounded-xl text-center shadow-sm">
                  <div className="flex justify-center mb-3 text-red-500">
                    <svg className="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                  </div>
                  <h3 className="text-lg font-bold mb-1">Connection Error</h3>
                  <p className="font-mono text-sm bg-red-100 py-1 px-2 rounded inline-block mb-4 border border-red-200">{error}</p>
                  <p className="text-sm opacity-80 mb-6 max-w-md mx-auto">
                    The application could not reach the API. Please ensure the API server is online and accessible from your network.
                  </p>
                  <button 
                    onClick={reloadMatches}
                    className="px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-bold text-sm transition-all shadow-md active:translate-y-0.5"
                  >
                    Retry Connection
                  </button>
               </div>
            )}

            {!isLoading && !error && matches.length === 0 && (
              <div className="p-12 text-center border-2 border-black rounded-2xl bg-gray-50">
                <p className="font-bold text-lg">No matches found</p>
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {pagedMatches.map((match) => (
                <MatchCard 
                  key={match.id} 
                  match={match} 
                  // eslint-disable-next-line eqeqeq
                  isActive={activeMatchId == match.id}
                  onWatch={watchMatch}
                  onUnwatch={unwatchMatch}
                />
              ))}
            </div>
            {!isLoading && !error && matches.length > pageSize && (
              <div className="flex flex-wrap items-center justify-between gap-3 pt-2">
                <span className="text-xs font-medium text-gray-500">
                  Page {currentPage} of {totalPages}
                </span>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
                    disabled={currentPage === 1}
                    className={`
                      px-3 py-1.5 rounded-lg text-xs font-bold border-2 border-black transition-all
                      ${currentPage === 1 ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white hover:bg-gray-50'}
                    `}
                  >
                    Prev
                  </button>
                  <button
                    onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
                    disabled={currentPage === totalPages}
                    className={`
                      px-3 py-1.5 rounded-lg text-xs font-bold border-2 border-black transition-all
                      ${currentPage === totalPages ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white hover:bg-gray-50'}
                    `}
                  >
                    Next
                  </button>
                </div>
              </div>
            )}
          </main>

          {/* Right Column: Live Feed (Sticky on Desktop) */}
          <aside className="lg:col-span-1 h-[500px] lg:h-[calc(100vh-140px)] lg:sticky lg:top-8">
            <LiveFeed messages={commentary} isActive={!!activeMatchId} isLoading={isCommentaryLoading} />
          </aside>

        </div>

        {/* Documentation / Verification Section */}
        <section className="mt-12 border-t-2 border-gray-200 pt-8">
          <div className="bg-white border-2 border-black rounded-2xl p-6">
            <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
              <span className="bg-black text-white w-6 h-6 flex items-center justify-center rounded-full text-xs">?</span>
              Testing & Verification
            </h3>
            <div className="grid md:grid-cols-2 gap-8 text-sm text-gray-600">
              <div>
                <h4 className="font-bold text-black mb-2">Configuration</h4>
                <ul className="list-disc list-inside space-y-1">
                  <li>REST URL: <code className="bg-gray-100 px-1 rounded">{API_BASE_URL}</code></li>
                  <li>WS URL: <code className="bg-gray-100 px-1 rounded">{WS_BASE_URL}</code></li>
                  <li>Modify these in <code className="bg-gray-100 px-1 rounded">constants.ts</code></li>
                </ul>
              </div>
              <div>
                <h4 className="font-bold text-black mb-2">How to Verify</h4>
                <p className="mb-2">1. Click the action button on any card (it shows "Watch Live" for live games).</p>
                <p className="mb-2">2. The status indicator top-right will turn green.</p>
                <p>3. Wait for <code className="text-xs bg-gray-100 p-0.5 border border-gray-300 rounded">score_update</code> or <code className="text-xs bg-gray-100 p-0.5 border border-gray-300 rounded">commentary</code> events from the server. The card score updates instantly, and the right panel fills with text.</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default App;
