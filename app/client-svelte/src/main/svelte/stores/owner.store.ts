import { writable } from "svelte/store";

const KEY = "storedOwner";

interface Payload {
  id?: string;
}

function payload(): Payload {
  return {
    id: undefined,
  };
}

export const storedOwner = writable<Payload>(
  JSON.parse(localStorage.getItem(KEY) || "null") || payload()
);

storedOwner.subscribe((value) => {
  if (typeof value === "object") {
    localStorage.setItem(KEY, JSON.stringify(value));
  }
});
