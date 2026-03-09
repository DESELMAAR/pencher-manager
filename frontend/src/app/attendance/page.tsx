'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export default function AttendancePage() {
  const [record, setRecord] = useState<{ attendanceStatus?: string; plannedStartTime?: string; actualStartTime?: string; delayMinutes?: number } | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    if (!token) return;
    fetch(`${API_BASE}/api/attendance/me/today`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.ok ? r.json() : null)
      .then(setRecord)
      .finally(() => setLoading(false));
  }, []);

  const status = record?.attendanceStatus ?? '—';
  const color = status === 'ON_TIME' ? 'green' : status === 'LATE' ? 'orange' : status === 'ABSENT' ? 'red' : 'gray';

  return (
    <main style={{ padding: '2rem' }}>
      <Link href="/dashboard">← Dashboard</Link>
      <h1>My attendance today</h1>
      {loading ? <p>Loading...</p> : (
        <div style={{ marginTop: '1rem' }}>
          <p><strong>Status:</strong> <span style={{ color }}>{status}</span></p>
          {record?.plannedStartTime && <p>Planned start: {record.plannedStartTime}</p>}
          {record?.actualStartTime && <p>Actual start: {record.actualStartTime}</p>}
          {record?.delayMinutes != null && record.delayMinutes > 0 && <p>Delay: {record.delayMinutes} min</p>}
        </div>
      )}
    </main>
  );
}
