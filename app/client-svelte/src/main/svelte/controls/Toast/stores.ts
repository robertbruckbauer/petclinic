import { writable } from "svelte/store";

export interface ToastEntry {
  msg: string;
  id: number;
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

  const push = (err: { detail?: string; toString: () => string }): number => {
    const entry: ToastEntry = {
      ...defaults,
      msg: err.detail || err.toString(),
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
