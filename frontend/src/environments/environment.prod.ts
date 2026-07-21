// Production environment.
// The app is served by nginx, which also reverse-proxies /api/* to the
// backend. An empty apiUrl means requests go to the SAME origin as the app
// (e.g. https://your-domain/api/...), so there is no CORS and no hardcoded
// backend host to maintain.
export const environment = {
  production: true,
  apiUrl: ''
};
