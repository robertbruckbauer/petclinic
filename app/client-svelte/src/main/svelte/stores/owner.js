import { writable } from "svelte/store";
// https://dev.to/danawoodman/svelte-quick-tip-connect-a-store-to-local-storage-4idi
const KEY = "storedNutzer";
export const storedOwner = writable(
  JSON.parse(localStorage.getItem(KEY)) || payload()
);
storedOwner.subscribe((value) => {
  if (typeof value === "object") {
    localStorage.setItem(KEY, JSON.stringify(value));
  }
});
function payload() {
  return {
    id: undefined,
  };
}
