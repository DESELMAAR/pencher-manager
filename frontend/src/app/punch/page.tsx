'use client';

import { useState } from 'react';
import Link from 'next/link';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export default function PunchPage() {
  const [msg, setMsg] = useState('');
  const [loading, setLoading] = useState<string | null>(null);

  async function punch(type: string) {
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    if (!token) { setMsg('Not logged in'); return; }
    setLoading(type);
    setMsg('');
    try {
      const res = await fetch(`${API_BASE}/api/punches/${type}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      if (res.ok) setMsg(`Punched: ${type}`);
      else setMsg(data.message || 'Failed');
    } catch {
      setMsg('Request failed');
    } finally {
      setLoading(null);
    }
  }

  return (
    <main style={{ padding: '2rem' }}>
      <Link href="/dashboard">← Dashboard</Link>
      <h1>Punch</h1>
      <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
        {['start-work', 'break-1', 'break-2', 'lunch-break', 'end-shift'].map((t) => (
          <button key={t} onClick={() => punch(t)} disabled={!!loading} style={{ padding: '0.75rem 1rem' }}>
            {loading === t ? '...' : t.replace(/-/g, ' ')}
          </button>
        ))}
      </div>
      {msg && <p style={{ marginTop: '1rem' }}>{msg}</p>}
    </main>
  );
}
