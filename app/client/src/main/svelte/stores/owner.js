import { writable } from "svelte/store";
export const storedOwnerId = writable(localStorage.storedOwnerId || null);
storedOwnerId.subscribe(v => localStorage.storedOwnerId = v)
