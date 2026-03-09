'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export default function DashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState<{ fullName?: string; email?: string; role?: string } | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    if (!token) {
      router.push('/login');
      return;
    }
    fetch(`${API_BASE}/api/auth/me`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error('Unauthorized');
        return res.json();
      })
      .then(setUser)
      .catch(() => router.push('/login'))
      .finally(() => setLoading(false));
  }, [router]);

  function logout() {
    localStorage.removeItem('accessToken');
    router.push('/login');
  }

  if (loading) return <p style={{ padding: '2rem' }}>Loading...</p>;

  return (
    <main style={{ padding: '2rem', maxWidth: 800 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <h1>Dashboard</h1>
        <button onClick={logout} style={{ padding: '0.5rem 1rem' }}>Logout</button>
      </div>
      <p>Welcome, {user?.fullName ?? user?.email} ({user?.role})</p>
      <nav style={{ marginTop: '2rem', display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
        <Link href="/departments" style={{ padding: '0.5rem 1rem', background: '#e5e7eb', borderRadius: 4 }}>Departments</Link>
        <Link href="/teams" style={{ padding: '0.5rem 1rem', background: '#e5e7eb', borderRadius: 4 }}>Teams</Link>
        <Link href="/employees" style={{ padding: '0.5rem 1rem', background: '#e5e7eb', borderRadius: 4 }}>Employees</Link>
        <Link href="/punch" style={{ padding: '0.5rem 1rem', background: '#e5e7eb', borderRadius: 4 }}>Punch</Link>
        <Link href="/attendance" style={{ padding: '0.5rem 1rem', background: '#e5e7eb', borderRadius: 4 }}>Attendance</Link>
      </nav>
    </main>
  );
}
