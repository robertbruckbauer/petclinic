import type { OwnerItem } from "./owner.type";
import type { PetItem } from "./pet.type";
import type { VetItem } from "./vet.type";

export interface Visit {
  id?: string;
  version: number;
  date: string;
  time?: string;
  duration?: string;
  text?: string;
  ownerItem?: OwnerItem;
  petItem?: PetItem;
  pet?: string | null;
  vetItem?: VetItem;
  vet?: string | null;
}
