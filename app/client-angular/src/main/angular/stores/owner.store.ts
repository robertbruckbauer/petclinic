import { signal } from "@angular/core";

const KEY = "storedOwner";

interface Payload {
  id?: string;
}

function payload(): Payload {
  return {
    id: undefined,
  };
}

const storedOwner = signal<Payload>(
  JSON.parse(localStorage.getItem(KEY) || "null") || payload()
);

export function getStoredOwner() {
  return storedOwner();
}

export function setStoredOwner(value: Payload) {
  storedOwner.set(value);
  localStorage.setItem(KEY, JSON.stringify(value));
}
