import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Pencher Manager',
  description: 'Workforce attendance and hierarchy management',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
