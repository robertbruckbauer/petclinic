import { OwnerItem } from "./owner.type";
import { PetItem } from "./pet.type";
import { VetItem } from "./vet.type";

export interface Visit {
  id?: string;
  version: number;
  date: string;
  time?: string;
  duration?: string;
  text: string;
  ownerItem?: OwnerItem;
  petItem?: PetItem;
  pet?: string | null;
  vetItem?: VetItem;
  vet?: string | null;
}
