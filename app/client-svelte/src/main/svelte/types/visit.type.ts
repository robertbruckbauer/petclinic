export interface OwnerItem {
  value: string;
  text: string;
}

export interface PetItem {
  value: string;
  text: string;
}

export interface VetItem {
  value: string;
  text: string;
}

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
