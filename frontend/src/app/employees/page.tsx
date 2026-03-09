'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export default function EmployeesPage() {
  const [list, setList] = useState<{ id: number; fullName: string; email: string; role: string }[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    if (!token) return;
    fetch(`${API_BASE}/api/users`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.ok ? r.json() : [])
      .then(setList)
      .finally(() => setLoading(false));
  }, []);

  return (
    <main style={{ padding: '2rem' }}>
      <Link href="/dashboard">← Dashboard</Link>
      <h1>Employees</h1>
      {loading ? <p>Loading...</p> : (
        <ul style={{ marginTop: '1rem', listStyle: 'none' }}>
          {list.map((u) => (
            <li key={u.id} style={{ padding: '0.5rem 0', borderBottom: '1px solid #eee' }}>{u.fullName} – {u.email} ({u.role})</li>
          ))}
          {list.length === 0 && <li>No users</li>}
        </ul>
      )}
    </main>
  );
}
