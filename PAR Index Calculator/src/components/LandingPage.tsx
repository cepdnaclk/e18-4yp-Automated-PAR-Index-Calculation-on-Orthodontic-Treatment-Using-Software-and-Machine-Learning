// src/components/LandingPage.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from 'react-bootstrap';
import { useTheme } from '../context/ThemeContext';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const { theme } = useTheme();

  const handleCalculateClick = () => {
    navigate('/upload');
  };

  return (
    <div className={`landing-page ${theme === 'light' ? 'light-theme' : 'dark-theme'}`}>
      <div className="content-center">
        <h1>Welcome to the PAR Index Calculator</h1>
        <Button onClick={handleCalculateClick} className="btn start">
          Start
        </Button>
      </div>
    </div>
  );
};

export default LandingPage;
