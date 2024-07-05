// src/components/ThemeToggle.tsx
import React from 'react';
import { Button } from 'react-bootstrap';
import { useTheme } from '../context/ThemeContext';
import { FaSun, FaMoon } from 'react-icons/fa';

const ThemeToggle: React.FC = () => {
  const { theme, toggleTheme } = useTheme();

  return (
    <Button
      className="btn btn-primary"
      onClick={toggleTheme}
      style={{ position: 'absolute', top: '20px', right: '20px' }}
    >
      {theme === 'light' ? <FaMoon /> : <FaSun />}
    </Button>
  );
};

export default ThemeToggle;
