import React, { useState, useEffect } from 'react';
import { 
  Button, 
  TextField, 
  Paper, 
  Table,
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Typography,
  Alert,
  Snackbar,
  Box,
  CircularProgress
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import axios from 'axios';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [name, setName] = useState('');
  const [selectedUser, setSelectedUser] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState({ open: false, message: '', severity: 'success' });

  // Fetch all users on component mount
  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/user');
      setUsers(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch users. Please try again.');
      console.error('Error fetching users:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = async () => {
    if (!name.trim()) {
      setNotification({
        open: true,
        message: 'Name cannot be empty',
        severity: 'error'
      });
      return;
    }

    setLoading(true);
    try {
      const response = await axios.post('/api/user', { name });
      setUsers([...users, response.data]);
      setName('');
      setNotification({
        open: true,
        message: 'User created successfully!',
        severity: 'success'
      });
    } catch (err) {
      setNotification({
        open: true,
        message: 'Failed to create user',
        severity: 'error'
      });
      console.error('Error creating user:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateUser = async () => {
    if (!name.trim() || !selectedUser) return;

    setLoading(true);
    try {
      const response = await axios.put(`/api/user/${selectedUser.id}`, { name });
      setUsers(users.map(user => 
        user.id === selectedUser.id ? response.data : user
      ));
      setName('');
      setSelectedUser(null);
      setOpenDialog(false);
      setNotification({
        open: true,
        message: 'User updated successfully!',
        severity: 'success'
      });
    } catch (err) {
      setNotification({
        open: true,
        message: 'Failed to update user',
        severity: 'error'
      });
      console.error('Error updating user:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async () => {
    if (!selectedUser) return;

    setLoading(true);
    try {
      await axios.delete(`/api/user/${selectedUser.id}`);
      setUsers(users.filter(user => user.id !== selectedUser.id));
      setSelectedUser(null);
      setOpenDeleteDialog(false);
      setNotification({
        open: true,
        message: 'User deleted successfully!',
        severity: 'success'
      });
    } catch (err) {
      setNotification({
        open: true,
        message: 'Failed to delete user',
        severity: 'error'
      });
      console.error('Error deleting user:', err);
    } finally {
      setLoading(false);
    }
  };

  const openEditDialog = (user) => {
    setSelectedUser(user);
    setName(user.name);
    setOpenDialog(true);
  };

  const openConfirmDeleteDialog = (user) => {
    setSelectedUser(user);
    setOpenDeleteDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setName('');
    setSelectedUser(null);
  };

  const handleCloseDeleteDialog = () => {
    setOpenDeleteDialog(false);
    setSelectedUser(null);
  };

  const handleCloseNotification = () => {
    setNotification({ ...notification, open: false });
  };

  return (
    <>
      <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Add New User
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <TextField
            label="Name"
            variant="outlined"
            value={name}
            onChange={(e) => setName(e.target.value)}
            fullWidth
            sx={{ mb: 2 }}
          />
          <Button
            variant="contained"
            color="primary"
            startIcon={<AddIcon />}
            onClick={handleCreateUser}
            disabled={loading}
            sx={{ mb: 2, minWidth: 120 }}
          >
            {loading ? <CircularProgress size={24} /> : 'Add User'}
          </Button>
        </Box>
      </Paper>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <TableContainer component={Paper} elevation={2}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Created At</TableCell>
              <TableCell>Updated At</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading && users.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  <CircularProgress />
                </TableCell>
              </TableRow>
            ) : users.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  No users found. Add a new user to get started.
                </TableCell>
              </TableRow>
            ) : (
              users.map((user) => (
                <TableRow key={user.id}>
                  <TableCell>{user.id}</TableCell>
                  <TableCell>{user.name}</TableCell>
                  <TableCell>{new Date(user.createdAt).toLocaleString()}</TableCell>
                  <TableCell>{new Date(user.updatedAt).toLocaleString()}</TableCell>
                  <TableCell align="center">
                    <Button
                      startIcon={<EditIcon />}
                      color="primary"
                      onClick={() => openEditDialog(user)}
                      sx={{ mr: 1 }}
                    >
                      Edit
                    </Button>
                    <Button
                      startIcon={<DeleteIcon />}
                      color="secondary"
                      onClick={() => openConfirmDeleteDialog(user)}
                    >
                      Delete
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Edit Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            type="text"
            fullWidth
            variant="outlined"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleUpdateUser} color="primary" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Update'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={openDeleteDialog} onClose={handleCloseDeleteDialog}>
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete {selectedUser?.name}? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDeleteDialog}>Cancel</Button>
          <Button onClick={handleDeleteUser} color="secondary" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Notification Snackbar */}
      <Snackbar 
        open={notification.open} 
        autoHideDuration={6000} 
        onClose={handleCloseNotification}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert 
          onClose={handleCloseNotification} 
          severity={notification.severity} 
          sx={{ width: '100%' }}
        >
          {notification.message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default UserManagement;
