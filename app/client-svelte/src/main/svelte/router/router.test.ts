import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { get } from "svelte/store";
import type { RouteParams, SvelteComponent, ActiveRoute } from "./router";

const mockWindow = {
  location: { pathname: "/", origin: "http://localhost:5050" },
  history: {
    pushState: vi.fn(),
    replaceState: vi.fn(),
  },
  addEventListener: vi.fn(),
  removeEventListener: vi.fn(),
};

const mockDocument = {
  addEventListener: vi.fn(),
  removeEventListener: vi.fn(),
};

describe("Router", () => {
  let activeRoute: any,
    register: any,
    navigate: any,
    startRouter: any,
    stopRouter: any,
    handleRouteChange: any;

  beforeEach(async () => {
    vi.clearAllMocks();
    vi.stubGlobal("window", mockWindow);
    vi.stubGlobal("document", mockDocument);
    mockWindow.location.pathname = "/";

    // The router functions depend on global browser objects
    // (window, document) that need to be mocked before the
    // router module is loaded. Dynamic imports also ensure
    // we get a fresh instance of the router for each test,
    // preventing test pollution between different test cases.
    const routerFn = await import("./router");
    ({
      activeRoute,
      register,
      navigate,
      startRouter,
      stopRouter,
      handleRouteChange,
    } = routerFn);
  });

  afterEach(() => {
    try {
      stopRouter();
    } catch (e) {
      // Ignore errors if router wasn't started
    }
    vi.unstubAllGlobals();
  });

  describe("Route Matching", () => {
    it("should match exact paths", () => {
      const homeComponent: SvelteComponent = class HomeComponent {};
      register({ path: "/", component: homeComponent });

      startRouter();

      const route: ActiveRoute = get(activeRoute);
      expect(route.path).toBe("/");
      expect(route.component).toBe(homeComponent);
      expect(route.params).toEqual({});
    });

    it("should match paths with parameters", () => {
      const userComponent: SvelteComponent = class UserComponent {};
      register({ path: "/user/:id", component: userComponent });

      // Simulate being on /user/123
      mockWindow.location.pathname = "/user/123";
      startRouter();

      const route: ActiveRoute = get(activeRoute);
      expect(route.path).toBe("/user/:id");
      expect(route.component).toBe(userComponent);
      expect(route.params).toEqual({ id: "123" });
    });

    it("should match paths with multiple parameters", () => {
      const postComponent: SvelteComponent = class PostComponent {};
      register({
        path: "/user/:userId/post/:postId",
        component: postComponent,
      });

      mockWindow.location.pathname = "/user/456/post/789";
      startRouter();

      const route: ActiveRoute = get(activeRoute);
      expect(route.path).toBe("/user/:userId/post/:postId");
      expect(route.component).toBe(postComponent);
      expect(route.params).toEqual({ userId: "456", postId: "789" });
    });

    it("should handle wildcard routes for not found", () => {
      const notFoundComponent: SvelteComponent = class NotFoundComponent {};
      register({ path: "*", component: notFoundComponent });

      mockWindow.location.pathname = "/nonexistent";
      startRouter();

      const route: ActiveRoute = get(activeRoute);
      expect(route.path).toBe("*");
      expect(route.component).toBe(notFoundComponent);
      expect(route.params).toEqual({});
    });
  });

  describe("Route Priority", () => {
    it("should match exact routes before wildcard", () => {
      const exactComponent: SvelteComponent = class ExactComponent {};
      const wildcardComponent: SvelteComponent = class WildcardComponent {};

      register({ path: "/exact", component: exactComponent });
      register({ path: "*", component: wildcardComponent });

      mockWindow.location.pathname = "/exact";
      startRouter();

      const route: ActiveRoute = get(activeRoute);
      expect(route.path).toBe("/exact");
      expect(route.component).toBe(exactComponent);
    });

    it("should fall back to wildcard when no exact match", () => {
      const exactComponent: SvelteComponent = class ExactComponent {};
      const wildcardComponent: SvelteComponent = class WildcardComponent {};

      register({ path: "/exact", component: exactComponent });
      register({ path: "*", component: wildcardComponent });

      mockWindow.location.pathname = "/unknown";
      startRouter();

      const route: ActiveRoute = get(activeRoute);
      expect(route.path).toBe("*");
      expect(route.component).toBe(wildcardComponent);
    });
  });

  describe("Navigation", () => {
    beforeEach(() => {
      const homeComponent: SvelteComponent = class HomeComponent {};
      const helpComponent: SvelteComponent = class HelpComponent {};
      const userComponent: SvelteComponent = class UserComponent {};

      register({ path: "/", component: homeComponent });
      register({ path: "/help", component: helpComponent });
      register({ path: "/user/:id", component: userComponent });

      startRouter();
    });

    it("should navigate to a new route", () => {
      // Mock the location change that would happen in a real browser
      const originalPathname = mockWindow.location.pathname;

      navigate("/help");

      expect(mockWindow.history.pushState).toHaveBeenCalledWith(
        {},
        "",
        "/help"
      );

      // Simulate the browser updating location.pathname after pushState
      mockWindow.location.pathname = "/help";

      // Manually trigger route change since we're in a test environment
      handleRouteChange();

      const route: ActiveRoute = get(activeRoute);
      expect(route.path).toBe("/help");

      // Reset for other tests
      mockWindow.location.pathname = originalPathname;
    });

    it("should navigate with replace option", () => {
      navigate("/help", true);

      expect(mockWindow.history.replaceState).toHaveBeenCalledWith(
        {},
        "",
        "/help"
      );
    });

    it("should navigate to route with parameters", () => {
      navigate("/user/123");

      expect(mockWindow.history.pushState).toHaveBeenCalledWith(
        {},
        "",
        "/user/123"
      );
    });

    it("should not navigate if already on the same path", () => {
      mockWindow.location.pathname = "/help";
      navigate("/help");

      expect(mockWindow.history.pushState).not.toHaveBeenCalled();
      expect(mockWindow.history.replaceState).not.toHaveBeenCalled();
    });

    it("should warn if router not started", () => {
      stopRouter();
      const consoleSpy = vi.spyOn(console, "warn").mockImplementation(() => {});

      navigate("/help");

      expect(consoleSpy).toHaveBeenCalledWith(
        "Router not started. Call startRouter() first."
      );
      expect(mockWindow.history.pushState).not.toHaveBeenCalled();

      consoleSpy.mockRestore();
    });
  });

  describe("Lifecycle", () => {
    it("should start router and set up event listeners", () => {
      startRouter();

      expect(mockWindow.addEventListener).toHaveBeenCalledWith(
        "popstate",
        expect.any(Function)
      );
      expect(mockDocument.addEventListener).toHaveBeenCalledWith(
        "click",
        expect.any(Function)
      );
    });

    it("should stop router and clean up event listeners", () => {
      startRouter();
      stopRouter();

      expect(mockWindow.removeEventListener).toHaveBeenCalledWith(
        "popstate",
        expect.any(Function)
      );
    });

    it("should not start router multiple times", () => {
      startRouter();
      startRouter(); // Second call should be ignored

      // Should only be called once
      expect(mockWindow.addEventListener).toHaveBeenCalledTimes(1);
    });

    it("should handle stopping router when not started", () => {
      stopRouter(); // Should not throw

      expect(mockWindow.removeEventListener).not.toHaveBeenCalled();
    });
  });
});
