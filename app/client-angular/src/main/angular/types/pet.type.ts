export interface Pet {
  id?: string;
  version: number;
  owner: string;
  name: string;
  born: string;
  species: string;
}

export interface PetItem {
  value: string;
  text: string;
}
