import React from 'react';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import UserManagement from './components/UserManagement';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2E5BBA',
    },
    secondary: {
      main: '#E74C3C',
    },
    background: {
      default: '#f5f5f5',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            Hybrid Architecture Demo
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" gutterBottom>
            Users are stored in the Master DB and read from the Replica DB
          </Typography>
        </Paper>
        <UserManagement />
      </Container>
    </ThemeProvider>
  );
}

export default App;
