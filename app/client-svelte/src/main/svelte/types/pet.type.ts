export interface OwnerItem {
  value: string;
  text: string;
}

export interface Pet {
  id?: string;
  version: number;
  ownerItem?: OwnerItem;
  owner?: string;
  name: string;
  born: string;
  species: string;
}

export interface PetItem {
  value: string;
  text: string;
}
