import React from 'react';
import { ConnectionStatus } from '../types';

interface StatusIndicatorProps {
  status: ConnectionStatus;
}

export const StatusIndicator: React.FC<StatusIndicatorProps> = ({ status }) => {
  const getConfig = () => {
    switch (status) {
      case 'connected':
        return { color: 'bg-green-400', text: 'Live Connected' };
      case 'connecting':
        return { color: 'bg-yellow-400', text: 'Connecting...' };
      case 'reconnecting':
        return { color: 'bg-orange-400', text: 'Reconnecting...' };
      case 'error':
        return { color: 'bg-red-500', text: 'Live Updates Unavailable' };
      default:
        return { color: 'bg-gray-300', text: 'Offline' };
    }
  };

  const config = getConfig();

  return (
    <div className="flex items-center gap-2 bg-white px-3 py-1.5 rounded-lg border-2 border-black shadow-[2px_2px_0px_0px_rgba(0,0,0,1)]">
      <div className={`w-3 h-3 rounded-full border border-black ${config.color} ${status === 'reconnecting' ? 'animate-pulse' : ''}`} />
      <span className="text-xs font-bold uppercase tracking-wide">{config.text}</span>
    </div>
  );
};