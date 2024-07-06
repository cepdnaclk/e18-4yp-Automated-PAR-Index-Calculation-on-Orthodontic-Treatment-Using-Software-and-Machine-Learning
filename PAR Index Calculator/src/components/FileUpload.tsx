import React, { useState } from 'react';
import axios from 'axios';
import { Spinner, Container, Form, Button } from 'react-bootstrap';
import { useTheme } from '../context/ThemeContext';
import pako from 'pako';
import './FileUpload.css';

const FileUpload: React.FC = () => {
  const [patientName, setPatientName] = useState('');
  const [buccalFile, setBuccalFile] = useState<File | null>(null);
  const [lowerFile, setLowerFile] = useState<File | null>(null);
  const [upperFile, setUpperFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [parIndex, setParIndex] = useState<number | null>(null);
  const { theme } = useTheme();

  const handleFileChange = (
    event: React.ChangeEvent<HTMLInputElement>,
    setFile: React.Dispatch<React.SetStateAction<File | null>>
  ) => {
    if (event.target.files && event.target.files[0]) {
      setFile(event.target.files[0]);
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!patientName || !buccalFile || !lowerFile || !upperFile) {
      alert('Please fill in the patient name and upload all three STL files.');
      return;
    }

    const formData = new FormData();
    formData.append('name', patientName);
    formData.append('buccal_stl', buccalFile);
    formData.append('lower_stl', lowerFile);
    formData.append('upper_stl', upperFile);

    try {
      setLoading(true);
      const response = await axios.post('http://3.6.62.207:8080/api/parindex/predict', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setParIndex(response.data);
    } catch (error) {
      console.error('Error uploading files:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className={`file-upload ${theme === 'light' ? 'light-theme' : 'dark-theme'}`}>
      <h1>Upload Patient Models</h1>
      <Form onSubmit={handleSubmit}>
      <Form.Group controlId="formPatientName" className="PatientName">
          <Form.Label>Patient Name :- </Form.Label>
          <Form.Control
            className='InputName'
            type="text"
            value={patientName}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPatientName(e.target.value)}
          />
        </Form.Group>

        <Form.Group controlId="formBuccalFile" className="fileTypes">
          <Form.Label>Buccal STL File</Form.Label>
          
          <Form.Label>Lower STL File</Form.Label>
          
          <Form.Label>Upper STL File</Form.Label>
          
        </Form.Group>

        <Form.Group controlId="formLowerFile" className="stlForm">
          <Form.Control
              className='Boxes'
              type="file"
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFileChange(e, setBuccalFile)}
          />
          <Form.Control
            className='Boxes'
            type="file"
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFileChange(e, setLowerFile)}
          />
          <Form.Control
            className='Boxes'
            type="file"
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFileChange(e, setUpperFile)}
          />
        </Form.Group>

        <Button type="submit" className="btn-calculate" disabled={loading}>
          Calculate PAR Index
        </Button>
      </Form>

      {loading && (
        <div className="spinner-overlay">
          <div className="spinner-container">
            <Spinner animation="border" role="status" className="custom-spinner">
              <span className="sr-only">Loading...</span>
            </Spinner>
          </div>
        </div>
      )}
      
      {parIndex !== null && (
        <div className="parScore">
          <h2>PAR Score is {parIndex}</h2>
        </div>
      )}
    </Container>
  );
};

export default FileUpload;
