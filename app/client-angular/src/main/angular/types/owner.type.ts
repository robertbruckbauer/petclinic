import { PetItem } from "./pet.type";

export interface Owner {
  id?: string;
  version: number;
  name: string;
  address: string;
  contact: string;
  allPetItem?: PetItem[];
  allPet?: string[];
}

export interface OwnerItem {
  value: string;
  text: string;
}
