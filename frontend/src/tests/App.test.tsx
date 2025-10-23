import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { configureStore } from '@reduxjs/toolkit';
import App from '../App';
import taskReducer from '../store/taskSlice';
import authReducer from '../store/authSlice';

const createTestStore = (initialState = {}) => {
  return configureStore({
    reducer: {
      tasks: taskReducer,
      auth: authReducer,
    },
    preloadedState: initialState,
  });
};

const renderWithProviders = (
  ui: React.ReactElement,
  { initialState = {}, ...renderOptions } = {}
) => {
  const store = createTestStore(initialState);
  
  const Wrapper = ({ children }: { children: React.ReactNode }) => (
    <Provider store={store}>
      <BrowserRouter>
        {children}
      </BrowserRouter>
    </Provider>
  );

  return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
};

test('renders login page when not authenticated', () => {
  renderWithProviders(<App />);
  expect(screen.getByText(/sign in/i)).toBeInTheDocument();
});

test('renders app with authentication', () => {
  const initialState = {
    auth: {
      user: { id: '1', username: 'test', email: 'test@example.com', role: 'USER' },
      token: 'mock-token',
      loading: false,
      error: null,
    },
  };
  
  renderWithProviders(<App />, { initialState });
  expect(screen.getByText(/task management/i)).toBeInTheDocument();
});