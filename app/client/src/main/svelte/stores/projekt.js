import { writable } from 'svelte/store';
export const storedProjektId = writable(localStorage.storedProjektId);
storedProjektId.subscribe(v => localStorage.storedProjektId = v);
