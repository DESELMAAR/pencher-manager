'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export default function DepartmentsPage() {
  const [list, setList] = useState<{ id: number; name: string; description?: string }[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    if (!token) return;
    fetch(`${API_BASE}/api/departments`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.ok ? r.json() : [])
      .then(setList)
      .finally(() => setLoading(false));
  }, []);

  return (
    <main style={{ padding: '2rem' }}>
      <Link href="/dashboard" style={{ marginBottom: '1rem', display: 'inline-block' }}>← Dashboard</Link>
      <h1>Departments</h1>
      {loading ? <p>Loading...</p> : (
        <ul style={{ marginTop: '1rem', listStyle: 'none' }}>
          {list.map((d) => (
            <li key={d.id} style={{ padding: '0.5rem 0', borderBottom: '1px solid #eee' }}>{d.name} {d.description && `– ${d.description}`}</li>
          ))}
          {list.length === 0 && <li>No departments</li>}
        </ul>
      )}
    </main>
  );
}
