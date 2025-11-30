import type { OwnerItem } from "./owner.type";
import type { PetItem } from "./pet.type";
import type { VetItem } from "./vet.type";

export interface Visit {
  id?: string;
  version: number;
  date: string;
  text: string;
  ownerItem?: OwnerItem;
  owner?: string;
  petItem?: PetItem;
  pet?: string;
  vetItem?: VetItem;
  vet?: string;
}
