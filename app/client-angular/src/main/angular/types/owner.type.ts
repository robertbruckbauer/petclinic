import { PetItem } from "./pet.type";

export interface Owner {
  id: string | undefined;
  version: number;
  name: string;
  address: string;
  contact: string;
  allPetItem: PetItem[];
}

export interface OwnerItem {
  value: string;
  text: string;
}
