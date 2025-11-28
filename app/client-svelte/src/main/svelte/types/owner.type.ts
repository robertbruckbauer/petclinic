export interface PetItem {
  value: string;
  text: string;
}

export interface Owner {
  id?: string;
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
