import { OwnerItem } from "./owner.type";
import { PetItem } from "./pet.type";
import { VetItem } from "./vet.type";

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
