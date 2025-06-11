export const toAbsoluteImageUrl = (path) => {
  if (!path) return '/no-image.png';
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
};
