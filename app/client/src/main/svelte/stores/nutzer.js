import { writable } from 'svelte/store';
export const storedNutzerId = writable(localStorage.storedNutzerId);
storedNutzerId.subscribe(v => localStorage.storedNutzerId = v);
