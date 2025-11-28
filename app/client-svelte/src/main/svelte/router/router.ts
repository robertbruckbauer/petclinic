import { writable, type Writable } from "svelte/store";

// Type definitions
export interface RouteParams {
  [key: string]: string;
}

export interface SvelteComponent {
  // Svelte 5 component type - can be any constructor function
  new (...args: any[]): any;
}

export interface RouteDefinition {
  path: string;
  component?: SvelteComponent;
}

export interface ActiveRoute {
  path: string;
  component?: SvelteComponent;
  params: RouteParams;
}

interface PathRegexResult {
  regex: RegExp;
  keys: string[];
}

interface RouteMatch {
  route: RouteDefinition;
  params: RouteParams;
  matchedPath: string;
}

// Store for the currently active route
export const activeRoute: Writable<ActiveRoute> = writable({
  path: "",
  params: {},
});

// Internal routes registry
const routes: Record<string, RouteDefinition> = {};

/**
 * Register a route with the router
 */
export function register(route: RouteDefinition): void {
  routes[route.path] = route;
}

/**
 * Convert a path pattern to a regular expression with parameter extraction
 */
function pathToRegex(path: string): PathRegexResult {
  const keys: string[] = [];

  // Handle wildcard route
  if (path === "*") {
    return { regex: /.*/, keys: [] };
  }

  // Replace :param with named capture groups
  const regexStr = path.replace(/:([^/]+)/g, (_, key: string) => {
    keys.push(key);
    return "([^/]+)";
  });

  // Ensure exact match
  const regex = new RegExp(`^${regexStr}$`);
  return { regex, keys };
}

/**
 * Extract parameters from a URL path using a route pattern
 */
function extractParams(path: string, pathname: string): RouteParams {
  const { regex, keys } = pathToRegex(path);
  const match = pathname.match(regex);

  if (!match) return {};

  const params: RouteParams = {};
  keys.forEach((key, index) => {
    params[key] = match[index + 1];
  });

  return params;
}

/**
 * Find the first matching route for a given pathname
 */
function findMatchingRoute(pathname: string): RouteMatch | null {
  // Try exact matches first
  for (const [path, route] of Object.entries(routes)) {
    if (path === "*") continue; // Skip wildcard for now

    const { regex } = pathToRegex(path);
    if (regex.test(pathname)) {
      const params = extractParams(path, pathname);
      return { route, params, matchedPath: path };
    }
  }

  // Fall back to wildcard route if no exact match
  const wildcardRoute = routes["*"];
  if (wildcardRoute) {
    return { route: wildcardRoute, params: {}, matchedPath: "*" };
  }

  return null;
}

/**
 * Handle route changes and update the active route store
 */
function handleRouteChange(): void {
  const pathname = window.location.pathname;
  const match = findMatchingRoute(pathname);

  if (match) {
    activeRoute.set({
      path: match.matchedPath,
      component: match.route.component,
      params: match.params,
    });
  }
}

// Router state
let isStarted = false;

/**
 * Start the router and set up event listeners
 */
export function startRouter(): void {
  if (isStarted) return;
  isStarted = true;

  // Handle initial route
  handleRouteChange();

  // Listen for browser navigation
  window.addEventListener("popstate", handleRouteChange);

  // Intercept link clicks for SPA navigation
  document.addEventListener("click", (e: MouseEvent) => {
    const target = e.target as HTMLElement;
    const link = target.closest("a");

    if (link && link.href && link.origin === window.location.origin) {
      const url = new URL(link.href);
      if (url.pathname !== window.location.pathname) {
        e.preventDefault();
        window.history.pushState({}, "", link.href);
        handleRouteChange();
      }
    }
  });
}

/**
 * Stop the router and clean up event listeners
 */
export function stopRouter(): void {
  if (!isStarted) return;
  isStarted = false;

  window.removeEventListener("popstate", handleRouteChange);
  // Note: We don't remove the click listener as it's harder to track
}

/**
 * Navigate programmatically to a given path
 * @param path - The path to navigate to (e.g., "/owner/123", "/help")
 * @param replace - If true, replace current history entry instead of pushing new one
 */
export function navigate(path: string, replace: boolean = false): void {
  if (!isStarted) {
    console.warn("Router not started. Call startRouter() first.");
    return;
  }

  // Don't navigate if we're already on this path
  if (window.location.pathname === path) {
    return;
  }

  // Update browser history
  if (replace) {
    window.history.replaceState({}, "", path);
  } else {
    window.history.pushState({}, "", path);
  }

  // Trigger route change
  handleRouteChange();
}

/**
 * Get the backend URL by replacing the frontend port (5050) with backend port (8080)
 */
export function backendUrl(): string {
  return (
    window.location.protocol +
    "//" +
    window.location.host.replace("5050", "8080")
  );
}

// Export for testing
export { findMatchingRoute, extractParams, pathToRegex, handleRouteChange };
