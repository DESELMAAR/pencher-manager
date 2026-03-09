import Link from 'next/link';

export default function Home() {
  return (
    <main style={{ padding: '2rem', textAlign: 'center' }}>
      <h1>Pencher Manager</h1>
      <p style={{ marginTop: '1rem', marginBottom: '2rem' }}>
        Workforce attendance and hierarchy management
      </p>
      <Link
        href="/login"
        style={{
          display: 'inline-block',
          padding: '0.75rem 1.5rem',
          background: '#2563eb',
          color: 'white',
          borderRadius: '0.5rem',
        }}
      >
        Login
      </Link>
    </main>
  );
}
