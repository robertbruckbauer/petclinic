import { writable } from "svelte/store";

export interface ToastEntry {
  id: number;
  message: string;
  duration: number;
  initial: number;
  next: number;
}

const defaults = {
  duration: 60000,
  initial: 1,
  next: 0,
};

const createToast = () => {
  const { subscribe, update } = writable<ToastEntry[]>([]);
  let count = 0;

  const push = (cause: any): number => {
    const entry: ToastEntry = {
      ...defaults,
      message: cause.detail || cause.toString(),
      id: ++count,
    };
    update((allEntry) => [entry, ...allEntry]);
    return count;
  };

  const pop = (id: number): void => {
    update((allEntry) => {
      if (!allEntry.length || id === 0) return [];
      return allEntry.filter((entry) => entry.id !== id);
    });
  };

  const set = (id: number): void => {
    update((allEntry) => {
      const entryIndex = allEntry.findIndex((entry) => entry.id === id);
      if (entryIndex > -1) {
        allEntry[entryIndex] = { ...allEntry[entryIndex], id };
      }
      return allEntry;
    });
  };

  return { subscribe, push, pop, set };
};

export const toast = createToast();
